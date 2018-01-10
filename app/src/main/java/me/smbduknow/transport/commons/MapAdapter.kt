package me.smbduknow.transport.commons

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import me.smbduknow.transport.R
import me.smbduknow.transport.model.Vehicle


class MapAdapter(
        val context: Context,
        val googleMap: GoogleMap
) {

    private var curMarker: Marker? = null
    private val markerCache: MutableMap<String, Marker> = mutableMapOf()

    private var cameraMoveListener:
            ((target: LatLng, bounds: LatLngBounds, zoom: Float, bearing: Float) -> Unit)? = null

    private var markerClickListener: (() -> Unit)? = null
    private var mapClickListener: (() -> Unit)? = null

    init {
        // styling Map
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_map))
        googleMap.isIndoorEnabled = false
        googleMap.isBuildingsEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false

        // enable getting location
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        // set event listeners
        googleMap.setOnMarkerClickListener { onMarkerClick(it) }
        googleMap.setOnMapClickListener { onMapClick() }
        googleMap.setOnCameraIdleListener { onCameraMove() }

        val spb = LatLng(59.845, 30.325)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spb, 13.5f))
    }

    fun setOnCameraMoveListener(listener: (target: LatLng, bounds: LatLngBounds, zoom: Float, bearing: Float) -> Unit) {
        this.cameraMoveListener = listener
    }

    fun setOnEventClickListener(listener: () -> Unit) {
        this.markerClickListener = listener
    }

    fun setOnMapClickListener(listener: () -> Unit) {
        this.mapClickListener = listener
    }

    fun setMarkers(items: List<Vehicle>) {
        items.forEach { vehicle ->
            if (!markerCache.containsKey(vehicle.id)) {
                val marker = googleMap.addMarker(context,
                        vehicle.type,
                        vehicle.label,
                        vehicle.latitude,
                        vehicle.longitude,
                        vehicle.bearing)

                markerCache.put(vehicle.id, marker)
            } else {
                markerCache[vehicle.id]?.updateMarker(context,
                        vehicle.type,
                        vehicle.label,
                        vehicle.latitude,
                        vehicle.longitude,
                        vehicle.bearing)
            }
        }
    }

    fun recycleMarkers() {
        val bounds = googleMap.projection.visibleRegion.latLngBounds
        val iterator = markerCache.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val marker = entry.value
            if (!bounds.contains(marker.position)) {
                marker.remove()
                iterator.remove()
            }
        }
    }

    fun moveCamera(target: LatLng, zoom: Float, bearing: Float) {
        if(!checkSameState(target, zoom, bearing))
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(
                    target, zoom, 0f, bearing
            )))
    }

    fun animateCamera(target: LatLng, zoom: Float, bearing: Float) {
        if(!checkSameState(target, zoom, bearing))
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(
                    target, zoom, 0f, bearing
            )))
    }

    fun zoomIn() = googleMap.animateCamera(CameraUpdateFactory.zoomIn())
    fun zoomOut() = googleMap.animateCamera(CameraUpdateFactory.zoomOut())

    fun findMe() = googleMap.myLocation

    /* private methods */

    private fun checkSameState(target: LatLng, zoom: Float, bearing: Float): Boolean {
        val current = googleMap.cameraPosition
        return target == current.target && zoom == current.zoom && bearing == current.bearing
    }

    // Event callbacks

    private fun onMapClick() {
        mapClickListener?.invoke()
    }

    private fun onMarkerClick(marker: Marker): Boolean {
        markerClickListener?.invoke()
        return true
    }

    private fun onCameraMove() = with(googleMap) {
        cameraMoveListener?.invoke(
                cameraPosition.target,
                projection.visibleRegion.latLngBounds,
                cameraPosition.zoom,
                cameraPosition.bearing
        )
    }


}