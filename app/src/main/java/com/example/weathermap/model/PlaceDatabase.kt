package com.example.weathermap.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weathermap.data.PlaceDBData

@Database(entities = [PlaceDBData::class], version = 1)
abstract class PlaceDatabase: RoomDatabase() {
    abstract fun placeDbDAO(): PlaceDBDAO
}