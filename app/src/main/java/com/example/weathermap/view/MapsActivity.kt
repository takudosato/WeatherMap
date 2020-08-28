package com.example.weathermap.view

import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.weathermap.R
import com.example.weathermap.data.PlaceDBData
import com.example.weathermap.data.WeatherData
import com.example.weathermap.databinding.ActivityMapsBinding
import com.example.weathermap.model.*
import com.example.weathermap.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //ViewModelクラスのインスタンス　
    //private val ahoaho: MapsViewModel by viewModels<MapsViewModel>()
    val viewModel = MapsViewModel(Repository())

    lateinit private var marker: WeatherMarker

    private lateinit var mSearchText: EditText

    //DBアクセス
    private lateinit var db: PlaceDatabase
    private lateinit var dao: PlaceDBDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_maps)

        //DataBindingの設定
        val binding: ActivityMapsBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_maps
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //データベースアクセス構築
        constructPlaceDB()

        //検索文字列入力
        mSearchText = findViewById<EditText>(R.id.input_sarch)

        ///////////////////////////////////////////////
        /**
         * ViewModelの天気情報が更新された時の処理
         */
        val weatherObserve = Observer<WeatherData> { newWeatherData ->
            // Update the UI, in this case, a TextView.
            Log.d("weatherObser", newWeatherData.cityname)

            val cu = CameraUpdateFactory.newLatLng(
                newWeatherData.latlng
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

            val cu = CameraUpdateFactory.newLatLng(
                newLatLng
            )
            mMap.moveCamera(cu)
            marker.addMarker(newLatLng)

            //VMクラスへ渡す（いずれ自動化したい）
            viewModel.setMapClickPos(newLatLng)
        }

        ///////////////////////////////////////////////
        //LiveDataのObserver登録
        viewModel.weatherStatus.observe(this, weatherObserve)
        viewModel.searchLatLng.observe(this, searchLatLngObserve)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * DBを構築する
     *
     */
    private fun constructPlaceDB() {
        this.db = Room.databaseBuilder(
            applicationContext,
            PlaceDatabase::class.java,
            "place.db"
        ).build()
        this.dao = this.db.placeDbDAO()
    }

    /**
     * Googlemap初期化処理
     *
     * @param googleMap
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        mMap.setMinZoomPreference(4.0f)
        mMap.setMaxZoomPreference(16.0f)

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
     * 起動時のカメラ位置を設定する
     *
     */
    private fun initCameraPos() {
        var latLng = LatLng(35.592061, 139.736607)
        var zoom = 10F

        GlobalScope.launch() {
            withContext(Dispatchers.IO) {
                val data = dao.getAll()
                if(data.isEmpty()){
                } else {
                    val dbdata = data[0]
                    latLng = LatLng(
                        dbdata.latitude, dbdata.longitude
                    )
                    zoom = dbdata.zoom
                }
            }
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
            dao.deleteAll()
            dao.insert(data)
        }
    }

}
