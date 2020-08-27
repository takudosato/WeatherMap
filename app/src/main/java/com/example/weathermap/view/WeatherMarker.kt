package com.example.weathermap.view

import com.example.weathermap.R
import com.example.weathermap.data.WeatherData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class WeatherMarker(val mMap: GoogleMap) {
    //地図上のマーカー
    var markeropt: Marker? = null

    fun addWeatherMarker(weatherdata: WeatherData) {

        //マーカーはひとつのみ表示なので、追加前に既存のマーカーを削除する
        removeWeatherMarker()

        //マーカを作成する
        markeropt = mMap.addMarker(
            MarkerOptions()
                .position(weatherdata.latlng)
                .title("都市名：　" + weatherdata.cityname)
                .snippet("現在の気温：　" + weatherdata.temp + "度")
                .icon(BitmapDescriptorFactory.fromResource(getIconID(weatherdata)))
        )
    }

    /**
     * マーカーのみを表示する
     *
     * @param latlng
     */
    fun addMarker(latlng: LatLng) {

        //マーカーはひとつのみ表示なので、追加前に既存のマーカーを削除する
        removeWeatherMarker()

        //マーカを作成する
        markeropt = mMap.addMarker(
            MarkerOptions()
                .position(latlng)
        )
    }

    /**
     * マーカーを削除する
     *
     */
    fun removeWeatherMarker() {
        markeropt?.remove()
        markeropt = null
    }

    /**
     * 天気IDから、対応するアイコンのIDを返す
     *
     * @param weatherdata
     * @return
     */
    fun getIconID(weatherdata: WeatherData) : Int  {

        // https://openweathermap.org/weather-conditions

        //Thunderstorm
        if ( 200 <= weatherdata.id && weatherdata.id < 300) {
            return R.drawable.ame
        }
        //Drizzle
        else if ( 300 <= weatherdata.id && weatherdata.id < 400) {
            return R.drawable.ame
        }
        //Rain
        else if ( 500 <= weatherdata.id && weatherdata.id < 600) {
            return R.drawable.ame
        }
        //Snow
        else if ( 500 <= weatherdata.id && weatherdata.id < 600) {
            return R.drawable.yuki
        }
        //Atmosphere
        else if ( 700 <= weatherdata.id && weatherdata.id < 800) {
            return R.drawable.kumori
        }
        //Clear
        else if ( weatherdata.id == 800) {
            return R.drawable.hare
        }
        //Clouds
        else if ( 801 <= weatherdata.id && weatherdata.id < 900) {
            return R.drawable.kumori
        }
        else {
            return R.drawable.kumori
        }
    }




}