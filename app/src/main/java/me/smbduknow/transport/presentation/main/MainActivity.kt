package me.smbduknow.transport.presentation.main

import android.Manifest
import android.location.Location
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import me.smbduknow.mvpblueprint.BasePresenterActivity
import me.smbduknow.mvpblueprint.PresenterFactory
import me.smbduknow.transport.R
import me.smbduknow.transport.presentation.misc.PermissedAction
import me.smbduknow.transport.presentation.geo.FusedLocationProvider
import me.smbduknow.transport.presentation.geo.LocationProvider

class MainActivity : BasePresenterActivity<MainMvpPresenter, MainMvpView>(), OnMapReadyCallback, MainMvpView {

    private var mapAdapter : MapAdapter? = null

    private var locationProvider: LocationProvider? = null


    private lateinit var nearbyAction: PermissedAction

    override fun onCreatePresenterFactory() = object : PresenterFactory<MainMvpPresenter>() {
        override fun create() = MainPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationProvider = FusedLocationProvider(this, locationListener)

        map_zoom_in.setOnClickListener { mapAdapter?.zoomIn() }
        map_zoom_out.setOnClickListener { mapAdapter?.zoomOut() }
        map_geolocation.setOnClickListener { nearbyAction.invoke(this) }

        nearbyAction = PermissedAction(
                Manifest.permission.ACCESS_FINE_LOCATION,
                { requestUserLocation() },
                { } // TODO toast with error message
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapAdapter = MapAdapter(this, googleMap).apply {
            setOnCameraMoveListener { _, bounds, _, _ ->
                presenter?.onMapBoundsChanged(bounds)
            }
        }
        presenter?.onMapReady()
    }

    override fun render(viewState: MainViewState) {
        mapAdapter?.recycleMarkers()
        mapAdapter?.setMarkers(viewState.vehicles)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        nearbyAction.handlePermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    // TODO separate this logic from UI

    private fun requestUserLocation() = locationProvider?.let {
        if(it.isAvailable) it.requestLastLocation()
    }


    private val locationListener = object : LocationProvider.OnRecieveLocationListener {
        override fun onProviderConnected() {
            requestUserLocation()
        }

        override fun onReceiveLocation(location: Location) {
            val latLng = LatLng(location.latitude, location.longitude)
            mapAdapter?.animateCamera(latLng, 13.5f, 0f)
        }

        override fun onReceiveFailed() {

        }
    }

}
