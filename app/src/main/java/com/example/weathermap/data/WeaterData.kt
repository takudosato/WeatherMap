package com.example.weathermap.data

import com.google.android.gms.maps.model.LatLng
import java.math.BigDecimal

/**
 * 指定した箇所の天気情報を保持する
 *
 * @property main
 * @property description
 * @property icon
 * @property country
 * @property cityname
 */
data class WeatherData(
    var latlng: LatLng = LatLng(0.0,0.0),
    var id: Int = 0,
    var temp: String = "",
    var main: String = "",
    var description: String = "",
    var icon: String = "",
    var country: String = "",
    var cityname: String = ""
)