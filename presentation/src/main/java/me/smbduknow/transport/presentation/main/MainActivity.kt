package me.smbduknow.transport.presentation.main

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import me.smbduknow.transport.App
import me.smbduknow.transport.R
import me.smbduknow.transport.presentation.misc.PermissedAction
import me.smbduknow.transport.presentation.misc.map.MapIconProvider
import javax.inject.Inject


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mapAdapter: MapAdapter? = null

    private lateinit var nearbyAction: PermissedAction

    @Inject
    lateinit var viewModelFactory: MapViewModelFactory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MapViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        map_zoom_in.setOnClickListener { mapAdapter?.zoomIn() }
        map_zoom_out.setOnClickListener { mapAdapter?.zoomOut() }
        map_geolocation.setOnClickListener { nearbyAction.invoke(this) }

        nearbyAction = PermissedAction(Manifest.permission.ACCESS_FINE_LOCATION,
                { viewModel.onRequestUserLocation() },
                { Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show() }
        )

        App.graph.injectTo(this)

        viewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onDestroy() {
        viewModel.stateLiveData.removeObserver(stateObserver)
        super.onDestroy()
    }

    private val stateObserver = Observer<MapViewState> { state ->
        state?.let {
            mapAdapter?.recycleMarkers()
            mapAdapter?.setMarkers(state.vehicles)
//            mapAdapter?.animateCamera(state.userLocation, 13.5f, 0f)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val iconProvider = MapIconProvider(this)
        mapAdapter = MapAdapter(this, googleMap, iconProvider).apply {
            setOnCameraMoveListener { _, bounds, _, _ ->
                viewModel.onUpdateMapScope(bounds)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        nearbyAction.handlePermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
