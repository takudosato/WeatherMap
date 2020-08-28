package com.example.weathermap.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.weathermap.data.PlaceDBData

@Dao
interface PlaceDBDAO {

    @Insert
    fun insert(placedata: PlaceDBData)

    @Query("select * from PlaceDBData")
    fun getAll(): List<PlaceDBData>

    @Query("delete from PlaceDBData")
    fun deleteAll()
}