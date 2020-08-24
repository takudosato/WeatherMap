package com.example.weathermap.model

import android.util.Log
import com.example.weathermap.data.WeatherData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL


class reps {
    var weather: String = ""
}

data class City(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String
) : Serializable

data class Weather(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
) : Serializable

data class Temp(
    @SerializedName("day") val day: Double,
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double
) : Serializable

data class WeatherList(
    @SerializedName("temp") val temp: Temp,
    @SerializedName("weather") val weather: Array<Weather>,
    @SerializedName("clouds") val clouds: Double,
    @SerializedName("dt") val dt: Long,
    @SerializedName("pressure") val pressure: Double,
    @SerializedName("speed") val speed: Double
) : Serializable

data class WeatherResponse(
    @SerializedName("list") val list: Array<WeatherList>,
    @SerializedName("city") val city: City
) : Serializable

interface IApiService {
    @GET("data/2.5/weather?q=London,uk&appid=439d4b804bc8187953eb36d2a8c26a02")
    open fun getttt(
    ): Call<List<reps>>
}

interface WeatherApi {
    @GET("forecast/daily?q=Paris&units=metric&cnt=5")
    fun getWeatherForFiveDays(@Query("appid") appId: String) : Call<WeatherResponse?>
}




class Repository {



    init {

    }

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


}