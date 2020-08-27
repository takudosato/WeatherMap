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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.weathermap.R
import com.example.weathermap.data.WeatherData
import com.example.weathermap.databinding.ActivityMapsBinding
import com.example.weathermap.model.Repository
import com.example.weathermap.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //ViewModelクラスのインスタンス　
    //private val ahoaho: MapsViewModel by viewModels<MapsViewModel>()
    val viewModel = MapsViewModel(Repository())

    lateinit private var marker: WeatherMarker

    private lateinit var mSearchText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_maps)

        //DataBindingの設定
        val binding: ActivityMapsBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_maps
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //検索文字列入力
        mSearchText = findViewById<EditText>(R.id.input_sarch)


        val weatherObserve = Observer<WeatherData> { newWeatherData ->
            // Update the UI, in this case, a TextView.
            Log.d("weatherObser", newWeatherData.cityname)

            val cu = CameraUpdateFactory.newLatLng(
                newWeatherData.latlng
            )
            mMap.moveCamera(cu)
            marker.addWeatherMarker(newWeatherData)

        }

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

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.weatherStatus.observe(this, weatherObserve)

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.searchLatLng.observe(this, searchLatLngObserve)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        mMap.setMinZoomPreference(4.0f)
        mMap.setMaxZoomPreference(10.0f)

        viewModel.geocoder = Geocoder(this, Locale.getDefault())

        //マーカークラスにgooglemapを渡す
        marker = WeatherMarker(mMap)

        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            /**
             * mapをタップされた場所を取得
             */
            override fun onMapClick(latlng :LatLng) {

                marker.addMarker(latlng)

                //VMクラスへ渡す（いずれ自動化したい）
                viewModel.setMapClickPos(latlng)
            }
        })


    }

}