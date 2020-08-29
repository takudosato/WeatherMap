package com.example.weathermap.view

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.room.Room
import com.example.weathermap.R
import com.example.weathermap.data.PlaceDBData
import com.example.weathermap.data.WeatherData
import com.example.weathermap.databinding.ActivityMapsBinding
import com.example.weathermap.model.PlaceDBDAO
import com.example.weathermap.model.PlaceDatabase
import com.example.weathermap.model.Repository
import com.example.weathermap.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //ViewModelクラスのインスタンス　
    lateinit var repsitory: Repository
    lateinit var viewModel: MapsViewModel

    //天気マーカーを表示するクラス
    private lateinit var marker: WeatherMarker

    //検索文字列EditBox
    private lateinit var mSearchText: EditText

    /**
     * OnCreate
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //////////////////////////////////////////////
        //設定処理
        viewModel  = MapsViewModel(applicationContext)

        //DataBindingの設定
        val binding: ActivityMapsBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_maps
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //検索文字列入力
        mSearchText = findViewById(R.id.input_sarch)

        //LiveData登録
        setLiveData()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Googlemap初期化処理
     *
     * @param googleMap
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMinZoomPreference(3.0f)
        mMap.setMaxZoomPreference(16.0f)

        //Geocoder（住所と位置情報を繋ぐを宣言）
        viewModel.geocoder = Geocoder(this, Locale.getDefault())

        //マーカークラスにgooglemapを渡す
        marker = WeatherMarker(mMap)

        //初期カメラ設定
        initCameraPos()

        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            /**
             * mapをタップされた場所を取得
             */
            override fun onMapClick(latlng :LatLng) {

                // 取り急ぎ、マーカーを表示
                marker.addMarker(latlng)

                //VMクラスへ渡す（いずれ自動化したい）
                viewModel.setMapClickPos(latlng)
            }
        })
    }

    /**
     * LiveDataを登録する
     *
     */
    private fun setLiveData() {
        ///////////////////////////////////////////////
        /**
         * ViewModelの天気情報が更新された時の処理
         */
        val weatherObserve = Observer<WeatherData> { newWeatherData ->
            // Update the UI, in this case, a TextView.
            Log.d("weatherObser", newWeatherData.cityname)

            val zoom = mMap.getCameraPosition().zoom
            val cu = CameraUpdateFactory.newLatLngZoom(
                newWeatherData.latlng, zoom
            )
            mMap.moveCamera(cu)
            marker.addWeatherMarker(newWeatherData)

            //DBへ登録
            insertPlaceDB(newWeatherData.latlng)

        }

        ///////////////////////////////////////////////
        /**
         * ViewModelの位置情報が更新された時の処理
         */
        val searchLatLngObserve = Observer<LatLng> { newLatLng ->
            // Update the UI, in this case, a TextView.
            Log.d("searchLatLngObserve", "latitude: " + newLatLng.latitude.toString())
            Log.d("searchLatLngObserve", "longitude: " + newLatLng.longitude.toString())

            val zoom = mMap.getCameraPosition().zoom //point
            val cu = CameraUpdateFactory.newLatLngZoom(
                newLatLng, zoom
            )

            //VMクラスへ渡す（いずれ自動化したい）
            viewModel.setMapClickPos(newLatLng)
        }

        ///////////////////////////////////////////////
        //LiveDataのObserver登録
        viewModel.weatherStatus.observe(this, weatherObserve)
        viewModel.searchLatLng.observe(this, searchLatLngObserve)
    }

    /**
     * 起動時のカメラ位置を設定する
     *
     */
    private fun initCameraPos() {
        lateinit var latLng: LatLng
        var zoom: Float = 0F //プリミティブ型

        GlobalScope.launch() {

            //DB登録されている、位置情報を取得する(未登録の場合、)
            withContext(Dispatchers.IO) {
                val data = viewModel.getPlaceDBData()
                latLng = LatLng(
                    data.latitude, data.longitude
                )
                zoom = data.zoom
            }
            //取得した位置情報を反映する
            withContext(Dispatchers.Main) {
                val cu = CameraUpdateFactory.newLatLngZoom(
                    latLng, zoom
                )
                mMap.moveCamera(cu)
                //VMクラスへ渡す（いずれ自動化したい）
                viewModel.setMapClickPos(latLng)
            }

        }
    }

    /**
     * DBに位置情報を登録する
     *
     * @param latlng
     */
    fun insertPlaceDB(latlng: LatLng) {

        Log.d("MapsActivity", "insertPlaceDB----------------------------------")

        //位置情報とzooom情報を登録
        val data = PlaceDBData(0,
            latlng.latitude,
            latlng.longitude,
            mMap.getCameraPosition().zoom
        )
        GlobalScope.launch(Dispatchers.IO) {

            //DBに登録
            viewModel.insertDBPlaceData(data)
        }
    }

    /**
     * 検索文字列の削除ボタン押下で、文字列を消去する
     */
    fun onClickDelete(view: View) {
        mSearchText.setText("")
    }

}
