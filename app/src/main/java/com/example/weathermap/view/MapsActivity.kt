package com.example.weathermap.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.weathermap.R
import com.example.weathermap.data.WeatherData
import com.example.weathermap.databinding.ActivityMapsBinding
import com.example.weathermap.model.Repository
import com.example.weathermap.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.coroutines.EmptyCoroutineContext.get


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    // Use the 'by viewModels()' Kotlin property delegate
    // from the activity-ktx artifact
    private val model: MapsViewModel by viewModels<MapsViewModel>()

    private lateinit var mMap: GoogleMap

    val viewModel = MapsViewModel(Repository())


    private val module: Module = module {
        viewModel { MapsViewModel(Repository.getInstance()) }
        single { Repository() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_maps)

        //DataBindingの設定

        val binding: ActivityMapsBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_maps
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this

        //Koinのスタート
        startKoin {
            modules(listOf(module))
        }



        // Create the observer which updates the UI.
        val nameObserver = Observer<String> { newName ->
            // Update the UI, in this case, a TextView.
            Log.d("aaaaaaaaa", newName)
        }

        val weatherObser = Observer<WeatherData> { newData ->
            // Update the UI, in this case, a TextView.
            Log.d("weatherObser", newData.cityname)
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.currentName.observe(this, nameObserver)

        viewModel.weatherStatus.observe(this, weatherObser)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            /**
             * mapをタップされた場所を取得
             */
            override fun onMapClick(latlng :LatLng) {

                //VMクラスへ渡す（いずれ自動化したい）
                viewModel.setMapClickPos(latlng)

                //val anotherName = "John Doe"
                //model.currentName.setValue(anotherName)

                //アイコンを表示する
                val descriptor =
                    BitmapDescriptorFactory.fromResource(R.drawable.hare)

                val options = GroundOverlayOptions()
                options.image(descriptor)

                options.position(latlng, 300f)


//地図に貼り付けます
                val overlay: GroundOverlay = mMap.addGroundOverlay(options)

//画像のアルファ値を設定、0で不透明。
                overlay.transparency = 0.3f

                //val location = LatLng(latlng.latitude,latlng.longitude)
                //mMap.addMarker(MarkerOptions().position(location))
            }
        })
    }
}