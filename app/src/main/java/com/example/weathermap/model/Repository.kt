package com.example.weathermap.model

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.weathermap.data.PlaceDBData
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


class Repository(context: Context) {

    //WebAPIアクセスクラス
    val owmap = OpenWeatherMapAccess()

    //位置情報DBアクセスクラス
    val placeDBaccrss = PlaceDBAccess(context)

    /**
     * 指定した緯度経度の天気情報を返す
     *
     * @param latlng
     * @return
     */
    fun getWeather(latlng : LatLng) : WeatherData {
        Log.d("getWeather", "latitude: " + latlng.latitude.toString())
        Log.d("getWeather", "longitude: " + latlng.longitude.toString())

        //天気情報をデータクラスに格納し、返す
        return owmap.getWeatherInfobyLatLng(latlng)
    }

    /**
     * DBに登録されている位置情報を返す
     */
    fun getPlaceDBData(): PlaceDBData {

        return placeDBaccrss.getPlaceDBData()
    }

    /**
     * 位置情報をDBに登録する
     *
     * @param data
     */
    fun insertDBPlaceData(data: PlaceDBData) {
        placeDBaccrss.insertDBPlaceData(data)
    }
}