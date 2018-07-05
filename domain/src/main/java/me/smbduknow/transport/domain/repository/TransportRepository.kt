package me.smbduknow.transport.domain.repository

import io.reactivex.Single
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Route
import me.smbduknow.transport.domain.model.Vehicle

interface TransportRepository {

    fun getAllVehicles(
            mapScope: MapScope,
            types: List<String>,
            routeId: String? = null
    ): Single<List<Vehicle>>

    fun searchRoutes(
            label: String
    ): Single<List<Route>>

}