package com.example.weathermap.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermap.data.WeatherData
import com.example.weathermap.model.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapsViewModel(
    private val loginRepository: Repository
): ViewModel() {

    var weatherStatus = WeatherData()

    fun click() {
        Log.d("test", "clicked")
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
                weatherStatus = wetherdata
            }
        }
    }
}