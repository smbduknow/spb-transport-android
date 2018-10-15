package me.smbduknow.transport.presentation.main

import com.google.android.gms.maps.model.LatLng
import me.smbduknow.transport.domain.model.Vehicle


data class MapViewState(
        val userLocation: LatLng,
        val vehicles: List<Vehicle> = emptyList(),
        val error: Throwable? = null
)