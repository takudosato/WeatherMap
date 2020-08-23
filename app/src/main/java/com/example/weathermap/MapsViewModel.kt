package com.example.weathermap

import android.util.Log
import androidx.lifecycle.ViewModel

class MapsViewModel : ViewModel() {

    fun click() {
        Log.d("test", "clicked")
    }
}