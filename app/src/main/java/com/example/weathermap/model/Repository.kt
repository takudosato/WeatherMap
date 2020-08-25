package com.example.weathermap.model

import android.util.Log
import com.example.weathermap.data.WeatherData
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL


class Repository {

    /**
     * 指定した緯度経度の天気情報を返す
     *
     * @param latlng
     * @return
     */
    fun getWeather(latlng : LatLng) : WeatherData {
        Log.d("getWeather", "1 " + latlng.latitude.toString())
        Log.d("getWeather", "2 " + latlng.longitude.toString())

        val owmap = OpenWeatherMapAccess()
        return owmap.getWeatherInfobyLatLng(latlng)
    }

    companion object {
        private var instance: Repository? = null

        fun getInstance(): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository().also { instance = it }
            }
    }


}