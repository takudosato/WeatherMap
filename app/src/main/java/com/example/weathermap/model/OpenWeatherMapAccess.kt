package com.example.weathermap.model

import android.R.attr
import android.util.Log
import com.example.weathermap.data.WeatherData
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class OpenWeatherMapAccess {

    fun getWeatherInfobyLatLng(latlng: LatLng): WeatherData {

        //経度緯度からURLを作成
        val url = makeURLbyLatLng(latlng)

        //コネクションを作成
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"

        //サイトにアクセスする
        con.connect()
        val stream = con.inputStream

        // JSON形式で結果が返るためパースのためにStringに変換する
        val result = is2String(stream)

        //JSONのパース
        val watherData = jsonPerse(result)
        return watherData
    }

    /**
     * 緯度経度から天気情報を取得するためのURLを返す
     *
     * @param latlng
     * @return
     */
    private fun makeURLbyLatLng(latlng: LatLng): URL {

        val urlStr =
            "https://api.openweathermap.org/data/2.5/weather?lat=" + latlng.latitude.toString() + "&lon=" + latlng.longitude.toString() + "&appid=3fb52c0587b90eec598d728ba7484f8a"

        return URL(urlStr)
    }

    private fun is2String(stream: InputStream): String {
        val sb = StringBuilder()

        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while (line != null) {
            sb.append(line)
            line = reader.readLine()
        }

        reader.close()

        return sb.toString()
    }

    /**
     * JSONから必要な情報を抜き出す
     *
     * @param data
     */
    fun jsonPerse(data: String): WeatherData {

        try {
            val rootJSON = JSONObject(data)

            //weather
            val weatherData = rootJSON.getString("weather")
            var arrayJson = JSONArray(weatherData)
            //val num = arrayJson.length()
            val weatherPart = arrayJson.getJSONObject(0)
            val main = weatherPart.getString("main")
            val description = weatherPart.getString("description")
            val icon = weatherPart.getString("icon")
            //country
            val country = rootJSON.getJSONObject("sys").getString("country")

            //name
            val name = rootJSON.getString("name")

            Log.i("天気", main)
            Log.i("天気2", description)

            return WeatherData(main, description, icon, country, name)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        //エラーの場合は空のデータを返す
        return WeatherData()
    }

}
