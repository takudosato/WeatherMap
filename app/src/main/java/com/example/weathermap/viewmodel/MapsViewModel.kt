package com.example.weathermap.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weathermap.model.Repository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapsViewModel: ViewModel() {

    var weatherStatus: Int = 0
    val repository = Repository()

    fun click() {
        Log.d("test", "clicked")
    }

    /**
     * タップされた位置を受け取る
     */
    fun setMapClickPos(latlng : LatLng) {
        Log.d("latitude", latlng.latitude.toString())
        Log.d("longitude", latlng.longitude.toString())

        weatherStatus = repository.getWeather(latlng)
    }
}