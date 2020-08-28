package com.example.weathermap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DBにセットする情報をまとめるデータクラス
 */
@Entity
data class PlaceDBData (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var zoom: Float = 0F
    )
{
}