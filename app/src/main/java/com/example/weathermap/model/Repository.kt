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

    //DBアクセス　コンストラクタで初期化するので、lateInitは不要
    private var db: PlaceDatabase
    private var dao: PlaceDBDAO

    /**
     * 初期化処理
     * DBを構築する
     */
    init {
        this.db = Room.databaseBuilder(
            context,
            PlaceDatabase::class.java,
            "place.db"
        ).build()
        this.dao = this.db.placeDbDAO()
    }

    /**
     * DBに登録されている位置情報を返す
     */
    fun getPlaceDBData(): PlaceDBData {
        val data = dao.getAll()
        if(!data.isEmpty()){
            return data[0]
        }
        else
        {
            //登録されていない場合の初期値(東京)
            return PlaceDBData(0, 35.592061, 139.736607, 10F)
        }
    }

    /**
     * 位置情報をDBに登録する
     *
     * @param data
     */
    fun insertDBPlaceData(data: PlaceDBData) {
        //登録するのは最後のひとつの場所だけ。それ以前のは削除する
        dao.deleteAll()

        //登録する
        dao.insert(data)
    }

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