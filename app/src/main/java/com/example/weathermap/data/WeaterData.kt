package com.example.weathermap.data

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
    val main: String = "",
    val description: String = "",
    val icon: String = "",
    val country: String = "",
    val cityname: String = ""
)