package me.smbduknow.transport.presentation.main

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import me.smbduknow.transport.R
import me.smbduknow.transport.domain.model.Vehicle
import me.smbduknow.transport.presentation.misc.map.MapIconProvider


class MapAdapter(
        context: Context,
        private val googleMap: GoogleMap,
        private val iconProvider: MapIconProvider
) {

    private var curMarker: Marker? = null
    private val markerCache: MutableMap<String, Marker> = mutableMapOf()

    private var userMarker: Marker? = null

    private var cameraMoveListener:
            ((target: LatLng, bounds: LatLngBounds, zoom: Float, bearing: Float) -> Unit)? = null

    private var markerClickListener: ((id: String) -> Unit)? = null
    private var mapClickListener: (() -> Unit)? = null

    init {
        // styling Map
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_map))
        googleMap.isIndoorEnabled = false
        googleMap.isBuildingsEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        // set event listeners
        googleMap.setOnMarkerClickListener { onMarkerClick(it) }
        googleMap.setOnMapClickListener { onMapClick() }
        googleMap.setOnCameraIdleListener { onCameraMove() }
    }

    fun setOnCameraMoveListener(listener: (target: LatLng, bounds: LatLngBounds, zoom: Float, bearing: Float) -> Unit) {
        this.cameraMoveListener = listener
    }

    fun setOnVehicleClickListener(listener: (id: String) -> Unit) {
        this.markerClickListener = listener
    }

    fun setOnMapClickListener(listener: () -> Unit) {
        this.mapClickListener = listener
    }

    fun setMarkers(items: List<Vehicle>) {
        items.forEach { vehicle ->
            val icon = iconProvider.getVehicleIcon(vehicle.type, vehicle.label, vehicle.bearing)
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon)
            val latLng = LatLng(vehicle.latitude, vehicle.longitude)

            if (!markerCache.containsKey(vehicle.id)) {
                markerCache[vehicle.id] = googleMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 0.5f)
                        .flat(true)
                        .icon(bitmapDescriptor)
                ).apply { tag = id }
            } else {
                markerCache[vehicle.id]?.apply {
                    position = latLng
                    setIcon(bitmapDescriptor)
                }
            }
        }
    }

    fun recycleMarkers(fullRefresh: Boolean = false) {
        if (fullRefresh) {
            googleMap.clear()
            markerCache.clear()
            userMarker = null
            return
        }
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

    fun setUserMarker(position: LatLng) {
        if (userMarker != null) {
            userMarker?.position = position
        } else {
            val icon = iconProvider.getUserIcon()
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon)
            userMarker = googleMap.addMarker(MarkerOptions()
                    .position(position)
                    .anchor(0.5f, 0.5f)
                    .flat(true)
                    .icon(bitmapDescriptor)
            )
        }
    }

    fun moveCamera(target: LatLng,
                   zoom: Float = googleMap.cameraPosition.zoom,
                   bearing: Float = googleMap.cameraPosition.bearing) {
        if (!checkSameState(target, zoom, bearing))
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(
                    target, zoom, 0f, bearing
            )))
    }

    fun animateCamera(target: LatLng,
                      zoom: Float = googleMap.cameraPosition.zoom,
                      bearing: Float = googleMap.cameraPosition.bearing) {
        if (!checkSameState(target, zoom, bearing))
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(
                    target, zoom, 0f, bearing
            )))
    }

    fun zoomIn() = googleMap.animateCamera(CameraUpdateFactory.zoomIn())
    fun zoomOut() = googleMap.animateCamera(CameraUpdateFactory.zoomOut())

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
        markerClickListener?.invoke(marker.tag as? String ?: "")
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