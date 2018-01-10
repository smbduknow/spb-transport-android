package me.smbduknow.transport.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.transit.realtime.GtfsRealtime
import kotlinx.android.synthetic.main.activity_main.*
import me.smbduknow.transport.R
import me.smbduknow.transport.commons.CSVUtil
import me.smbduknow.transport.commons.MapAdapter
import me.smbduknow.transport.geo.FusedLocationProvider
import me.smbduknow.transport.geo.LocationProvider
import me.smbduknow.transport.model.Route
import me.smbduknow.transport.model.Vehicle
import java.io.IOException
import java.net.URL
import java.util.*

class MainActivity : FragmentActivity(), OnMapReadyCallback {

    private var mapAdapter : MapAdapter? = null

    private var routes: List<Route>? = null

    private var locationProvider: LocationProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        routes = CSVUtil.readCsv(this)

        locationProvider = FusedLocationProvider(this, locationListener)

        map_zoom_in.setOnClickListener { mapAdapter?.zoomIn() }
        map_zoom_out.setOnClickListener { mapAdapter?.zoomOut() }
//        map_geolocation.setOnClickListener { mapAdapter?.zoomOut() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapAdapter = MapAdapter(this, googleMap).apply {
            setOnCameraMoveListener { target, bounds, zoom, bearing ->
                onCameraChange(bounds)
            }
        }
    }

    private fun onCameraChange(bounds: LatLngBounds) {
        mapAdapter?.recycleMarkers()

        val coordsString = String.format(Locale.US, "%.4f,%.4f,%.4f,%.4f",
                bounds.southwest.longitude, bounds.southwest.latitude,
                bounds.northeast.longitude, bounds.northeast.latitude
        )
        TransportTask(this, mapAdapter!!, coordsString).execute()
    }


    inner class TransportTask(
            private val activity: Activity,
            private val adapter: MapAdapter,
            private val coordsString: String
    ) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            try {
                val url = URL("http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle?" +
                        "bbox=" + coordsString + "&transports=bus,trolley,tram")
                Log.d("transport", url.toString())
                val feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                val vehicles = feed.entityList.map {
                            val route = findRoute(it.vehicle.trip.routeId)
                            Vehicle(
                                    id = it.id,
                                    label = route?.label ?: "",
                                    type = route?.typeLabel ?: "",
                                    latitude = it.vehicle.position.latitude.toDouble(),
                                    longitude = it.vehicle.position.longitude.toDouble(),
                                    bearing = it.vehicle.position.bearing
                            )
                        }
                activity.runOnUiThread {
                    mapAdapter?.setMarkers(vehicles)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    fun findRoute(routeId: String): Route? {
        val pos = Collections.binarySearch(routes!!, Route(id = routeId))
        return if (pos >= 0) routes!![pos] else null
    }


    private fun requestUserLocation() {
        checkPermission()
    }


    private val locationListener = object : LocationProvider.OnRecieveLocationListener {
        override fun onProviderConnected() {
            requestUserLocation()
        }

        override fun onReceiveLocation(location: Location) {
            val latLng = LatLng(location.latitude, location.longitude)
            mapAdapter?.moveCamera(latLng, 13.5f, 0f)
        }

        override fun onReceiveFailed() {

        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted()
            }
        }
    }

    private fun permissionGranted() {
        if (locationProvider != null && locationProvider!!.isAvailable) {
            locationProvider!!.requestLastLocation()
        }
    }

    companion object {
        private val REQUEST_CODE_PERMISSION = 1
    }
}
