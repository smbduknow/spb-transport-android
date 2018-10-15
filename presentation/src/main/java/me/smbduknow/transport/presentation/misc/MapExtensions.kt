package me.smbduknow.transport.presentation.misc

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import me.smbduknow.transport.R


fun GoogleMap.addMarker(ctx: Context,
                        id: String,
                        type: String?,
                        label: String?,
                        lat: Double,
                        lon: Double,
                        bearing: Float) : Marker {

    val icon = resolveMarkerIcon(ctx, type, label, bearing)
    val position = LatLng(lat, lon)

    return this.addMarker(MarkerOptions()
            .position(position)
            .anchor(0.5f, 0.5f)
            .flat(true)
            .icon(icon)
    ).apply { tag = id }
}

fun Marker.updateMarker(ctx: Context,
                           type: String?,
                           label: String?,
                           lat: Double,
                           lon: Double,
                           bearing: Float) : Marker {

    val icon = resolveMarkerIcon(ctx, type, label, bearing)
    val position = LatLng(lat, lon)

    return this.apply {
        this.position = position
        this.setIcon(icon)
    }

}

private val RES_PIN_BUS = R.drawable.ic_vehicle_bus
private val RES_PIN_TROLLEY = R.drawable.ic_vehicle_trolley
private val RES_PIN_TRAM = R.drawable.ic_vehicle_tram

private fun resolveMarkerIcon(ctx: Context, type: String?, label: String?, bearing: Float): BitmapDescriptor {
    val iconRes: Int = when (type) {
        "bus" -> RES_PIN_BUS
        "trolley" -> RES_PIN_TROLLEY
        "tram" -> RES_PIN_TRAM
        else -> RES_PIN_BUS
    }
    val iconBitmap = DrawableUtil.createVehiclePin(ctx, iconRes, label ?: "", bearing)
    return BitmapDescriptorFactory.fromBitmap(iconBitmap)
}
