package com.example.weathermap.model

import android.util.Log
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


    fun getWeather(latlng : LatLng) : Int {
        Log.d("getWeather", "1 " + latlng.latitude.toString())
        Log.d("getWeather", "2 " + latlng.longitude.toString())

        //都市ID
        //val id = params[0]

        //val urlStr = "https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=3fb52c0587b90eec598d728ba7484f8a"

        val urlStr = "https://api.openweathermap.org/data/2.5/weather?lat=" + latlng.latitude.toString() + "&lon=" + latlng.longitude.toString() + "&appid=3fb52c0587b90eec598d728ba7484f8a"

        val url = URL(urlStr)
        val con =url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"

        con.connect()

        val stream = con.inputStream

        val result = is2String(stream)

        val rootJSON = JSONObject(result)
        val weatherData = rootJSON.getString("weather")
        Log.i("天気情報", weatherData)

        val arrayJson = JSONArray(weatherData)

        var main: String = ""
        var description: String = ""

        val num = arrayJson.length()

        // for(i in 0..num)
        //{
        val weatherPart = arrayJson.getJSONObject(0)
        main = weatherPart.getString("main")
        description = weatherPart.getString("description")
        //}

        Log.i("天気", main)
        Log.i("天気2", description)



        return 1
    }

    private fun is2String(stream: InputStream): String{
        val sb = StringBuilder()

        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while(line != null) {
            sb.append(line)
            line = reader.readLine()
        }

        reader.close()

        return sb.toString()

    }
}