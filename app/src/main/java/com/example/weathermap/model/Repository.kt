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
        Log.d("getWeather", "latitude: " + latlng.latitude.toString())
        Log.d("getWeather", "longitude: " + latlng.longitude.toString())

        //WebAPIにアクセスし、情報を取得
        val owmap = OpenWeatherMapAccess()

        //天気情報をデータクラスに格納し、返す
        return owmap.getWeatherInfobyLatLng(latlng)
    }

}