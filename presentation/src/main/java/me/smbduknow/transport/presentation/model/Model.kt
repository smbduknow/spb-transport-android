package me.smbduknow.transport.presentation.model

import com.google.android.gms.maps.model.LatLng

data class MapState(
        val target: LatLng,
        val bearing: Float,
        val zoom: Float
)