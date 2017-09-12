package me.smbduknow.transport

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.transit.realtime.GtfsRealtime
import me.smbduknow.transport.commons.CSVUtil
import me.smbduknow.transport.commons.DrawableUtil
import me.smbduknow.transport.geo.FusedLocationProvider
import me.smbduknow.transport.geo.LocationProvider
import me.smbduknow.transport.model.Route
import java.io.IOException
import java.net.URL
import java.util.*

class MainActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private var mMap: GoogleMap? = null

    private val searchRoute = Route()
    private var routes: List<Route>? = null

    private var markers: MutableMap<String, Marker> = HashMap()

    private var locationProvider: LocationProvider? = null

    private var millis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        routes = CSVUtil.readCsv(this)

        locationProvider = FusedLocationProvider(this, locationListener)

        millis = System.currentTimeMillis()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val spb = LatLng(59.845, 30.325)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(spb, 13.5f))
        mMap!!.setOnCameraChangeListener(this)
    }

    override fun onCameraChange(cameraPosition: CameraPosition) {
        if (System.currentTimeMillis() < millis + 1000) return

        val cameraBounds = mMap!!.projection.visibleRegion.latLngBounds

        val it = markers.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            val marker = entry.value
            if (!cameraBounds.contains(marker.position)) {
                marker.remove()
                it.remove()
            }
        }

        val coordsString = String.format(Locale.US, "%.4f,%.4f,%.4f,%.4f",
                cameraBounds.southwest.longitude, cameraBounds.southwest.latitude,
                cameraBounds.northeast.longitude, cameraBounds.northeast.latitude
        )

        TransportTask(this, mMap!!, coordsString).execute()
    }


    inner class TransportTask(private val activity: Activity, private val map: GoogleMap, private val coordsString: String) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            try {
                val url = URL("http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle?" +
                        "bbox=" + coordsString + "&transports=bus,trolley,tram,ship")
                Log.d("transport", url.toString())
                val feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                for (entity in feed.entityList) {
                    val bearing = entity.vehicle.position.bearing
                    val routeId = entity.vehicle.trip.routeId
                    val pos = LatLng(entity.vehicle.position.latitude.toDouble(), entity.vehicle.position.longitude.toDouble())
                    activity.runOnUiThread {
                        val route = findRoute(routeId)
                        var res = R.drawable.ic_bus
                        if (route?.typeLabel == "tram") res = R.drawable.ic_tram
                        if (route?.typeLabel == "trolley") res = R.drawable.ic_troll
                        val bd = DrawableUtil.writeOnDrawable(applicationContext, res, route?.label ?: "", -90 + bearing)
                        val btmp = BitmapDescriptorFactory.fromBitmap(bd.bitmap)
                        if (!markers.containsKey(entity.id)) {
                            val marker = map.addMarker(MarkerOptions().position(pos).icon(btmp).anchor(0.5f, 0.5f))
                            markers.put(entity.id, marker)
                        } else {
                            val marker = markers[entity.id]!!
                            marker.position = pos
                            marker.setIcon(btmp)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }
    }

    fun findRoute(routeId: String): Route? {
        searchRoute.id = routeId
        val pos = Collections.binarySearch(routes!!, searchRoute)
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
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f))
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
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.size >= 1) {
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
