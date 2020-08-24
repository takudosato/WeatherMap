package com.example.weathermap.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.weathermap.viewmodel.MapsViewModel
import com.example.weathermap.R
import com.example.weathermap.databinding.ActivityMapsBinding
import com.example.weathermap.model.Repository

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    val viewModel = MapsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_maps)

        val binding: ActivityMapsBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_maps
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this

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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            /**
             * mapをタップされた場所を取得
             */
            override fun onMapClick(latlng :LatLng) {

                //VMクラスへ渡す（いずれ自動化したい）
                viewModel.setMapClickPos(latlng)

                //val location = LatLng(latlng.latitude,latlng.longitude)
                //mMap.addMarker(MarkerOptions().position(location))
            }
        })
    }
}