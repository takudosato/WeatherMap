package com.example.weathermap

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermap.data.PlaceDBData
import com.example.weathermap.model.Repository
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class getPlaceDBDataTest {

    @Test
    @Throws(Exception::class)
    fun writePlaceReadData() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = Repository(context)

        val testid = 2
        val testlatitude = 10.11
        val testlongitude = 22.22
        val testzoom = 4F

        val placedata = PlaceDBData(
            testid,
            testlatitude,
            testlongitude,
            testzoom)
        repository.insertDBPlaceData(placedata)

        val ret = repository.getPlaceDBData()
        assertEquals(testid, ret.id)
        assertEquals(testlatitude, ret.latitude, 0.0)
        assertEquals(testlongitude, ret.longitude, 0.0)
        assertEquals(testzoom, ret.zoom)

    }

    @Test
    @Throws(Exception::class)
    fun getWeatherTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = Repository(context)

        val testlatitude = 10.11
        val testlongitude = 22.22
        val latlng = LatLng(testlatitude, testlongitude)


        val ret = repository.getWeather(latlng)
        assertEquals(testlatitude, ret.latlng.latitude, 0.0)
        assertEquals(testlongitude, ret.latlng.longitude, 0.0)
    }
}

