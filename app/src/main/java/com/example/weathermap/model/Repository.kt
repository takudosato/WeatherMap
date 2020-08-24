package com.example.weathermap.model

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class reps {
    var weather: String = ""
}

interface IApiService {
    @GET("data/2.5/weather?lat=35&lon=139&appid=439d4b804bc8187953eb36d2a8c26a02")
    open fun getttt(
        @Path("path") path: String?,
        @Query("q") name: String?,
        @Query("appid") key: String?
    ): Call<List<reps>>
}

class Repository {

    fun getWeather(latlng : LatLng) : Int {
        Log.d("getWeather", "1 " + latlng.latitude.toString())
        Log.d("getWeather", "2 " + latlng.longitude.toString())


        val retrofit = Retrofit.Builder()
            .baseUrl("https://samples.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(IApiService::class.java)

        val API_KEY: String = "439d4b804bc8187953eb36d2a8c26a02"
        service.getttt("weather", "London,uk", API_KEY).enqueue(object: Callback<List<reps>>{
            override fun onFailure(call: Call<List<reps>>, t: Throwable) {
                Log.d("error",t.message.toString())
            }

            override fun onResponse(call: Call<List<reps>>, response: Response<List<reps>>) {
                Log.d("success", response.body().toString())
            }

        })

        return 1
    }
}