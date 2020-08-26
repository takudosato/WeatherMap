package com.example.weathermap.viewmodel

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
import com.example.weathermap.data.WeatherData
import com.example.weathermap.model.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MapsViewModel( private val loginRepository: Repository): ViewModel() {

    var searchPlaceString = ""

    lateinit var geocoder: Geocoder

    val editorActionListener: TextView.OnEditorActionListener

    init {
        this.editorActionListener = TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.action == KeyEvent.ACTION_DOWN ||
                event.action == KeyEvent.KEYCODE_ENTER) {
                //文字列化実行
                geoLocate()
            }

            false
        }
    }

    companion object {

        //検索TextEditの通知を受け取る
        @BindingAdapter("onEditorActionListener")
        fun bindOnEditorActionListener(editText: EditText, editorActionListener: TextView.OnEditorActionListener) {
            editText.setOnEditorActionListener(editorActionListener)
        }
    }

    val weatherStatus: MutableLiveData<WeatherData> by lazy {
        MutableLiveData<WeatherData>()
    }


    fun geoLocate(): Boolean {
        Log.d("MapActivity", "geoLocate")

        val list = geocoder.getFromLocationName(searchPlaceString, 1)
        if (list.isNotEmpty() && list.size > 0) {
            val address = list.get(0)
            Log.d("geoLocate", "address: " + address.toString())
        }
        return true
    }

    /**
     * タップされた位置を受け取る
     */
    fun setMapClickPos(latlng : LatLng) {
        Log.d("latitude", latlng.latitude.toString())
        Log.d("longitude", latlng.longitude.toString())

        //Repositoryクラスの処理を、コルーチンで実行
        viewModelScope.launch(Dispatchers.IO) {
            val wetherdata = loginRepository.getWeather(latlng)
            //データが空（取得に失敗）でなければ、パラメータに反映する
            if (wetherdata != WeatherData()) {
                weatherStatus.postValue(wetherdata)
            }
        }
    }

    operator fun invoke(function: () -> MapsViewModel) {

    }
}