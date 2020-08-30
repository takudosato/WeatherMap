package com.example.weathermap.viewmodel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermap.data.PlaceDBData
import com.example.weathermap.data.WeatherData
import com.example.weathermap.model.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapsViewModel(context: Context): ViewModel() {

    private val repository =  Repository(context)

    //位置情報取得クラス
    lateinit var geocoder: Geocoder

    //検索文字列とリスナ
    var searchPlaceString = ""
    val editorActionListener: TextView.OnEditorActionListener

    //---------------------------------------
    // LiveData宣言
    // 指定した場所の天気情報
    val weatherStatus: MutableLiveData<WeatherData> by lazy {
        MutableLiveData<WeatherData>()
    }
    // 位置変更（WeatherAPIにアクセスする間に移動やマーカの表示を行うために使用する）
    val searchLatLng: MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }

    //------------------------------------------
    // 検索文字列のデータバインディング
    companion object {
        //検索TextEditの通知を受け取る
        @BindingAdapter("onEditorActionListener")
        fun bindOnEditorActionListener(editText: EditText, editorActionListener: TextView.OnEditorActionListener) {
            editText.setOnEditorActionListener(editorActionListener)
        }
    }
    /**
     * 文字列入力時のDataBind
     */
    init {
        //文字列検索EditTextで入力終了ボタンが押下された場合のリスナー
        this.editorActionListener = TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.action == KeyEvent.ACTION_DOWN ||
                event.action == KeyEvent.KEYCODE_ENTER) {
                    //文字列から位置情報を取得する
                    geoLocate()
                }

            false
        }
    }

    /**
     * 場所文字列から位置情報を取得する。
     * 呼び出される環境に必要であるため、trueを返す
     */
    fun geoLocate(): Boolean {
        Log.d("MapViewModel", "geoLocate")

            val list = geocoder.getFromLocationName(searchPlaceString, 1)
            if (list.isNotEmpty() && list.size > 0) {
                val address = list.get(0)
                Log.d("geoLocate", "address: " + address.toString())

                val latlng = LatLng(address.latitude.toDouble(), address.longitude.toDouble())
                searchLatLng.postValue(latlng)
            }

        return true
    }

    /**
     * 指定の位置の天気情報を受け取り、メンバ変数に設定する
     */
    fun setMapClickPos(latlng : LatLng) {
        Log.d("latitude", latlng.latitude.toString())
        Log.d("longitude", latlng.longitude.toString())

        //Repositoryクラスの処理を、コルーチンで実行
        viewModelScope.launch(Dispatchers.IO) {
            val wetherdata = repository.getWeather(latlng)
            //データが空（取得に失敗）でなければ、パラメータに反映する
            if (wetherdata != WeatherData()) {
                weatherStatus.postValue(wetherdata)
            }
        }
    }

    /**
     * DBの位置情報を取得
     */
    fun getPlaceDBData(): PlaceDBData {
        return repository.getPlaceDBData()
    }

    /**
     * DBに位置情報を登録
     */
    fun insertDBPlaceData(data: PlaceDBData) {
        repository.insertDBPlaceData(data)
    }

}