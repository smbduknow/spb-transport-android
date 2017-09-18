package me.smbduknow.transport.commons

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import me.smbduknow.transport.R

private val RES_PIN_BUS = R.drawable.ic_vehicle_bus
private val RES_PIN_TROLLEY = R.drawable.ic_vehicle_trolley
private val RES_PIN_TRAM = R.drawable.ic_vehicle_tram

val GoogleMap.markerCache: MutableMap<String, Marker>
    get() = mutableMapOf()

fun GoogleMap.addMarker(ctx: Context,
                        id: String,
                        type: String?,
                        label: String?,
                        lat: Double,
                        lon: Double,
                        bearing: Float) : Marker {

    val iconRes: Int = when (type) {
        "bus" -> RES_PIN_BUS
        "trolley" -> RES_PIN_TROLLEY
        "tram" -> RES_PIN_TRAM
        else -> RES_PIN_BUS
    }
    val iconBitmap = DrawableUtil.createVehiclePin(ctx, iconRes, label ?: "", bearing)
    val icon = BitmapDescriptorFactory.fromBitmap(iconBitmap)

    val position = LatLng(lat, lon)

    if (!markerCache.containsKey(id)) {
        val marker = this.addMarker(MarkerOptions()
                .position(position)
                .anchor(0.5f, 0.5f)
                .flat(true)
                .icon(icon)
        )
        markerCache.put(id, marker)

        return marker
    } else {
        val marker = markerCache[id]!!
        marker.position = position
        marker.setIcon(icon)

        return marker
    }
}

fun GoogleMap.recycleMarkers() {
    val bounds = projection.visibleRegion.latLngBounds
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
