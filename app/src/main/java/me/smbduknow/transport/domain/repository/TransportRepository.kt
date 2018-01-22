package me.smbduknow.transport.domain.repository

import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Vehicle
import rx.Observable

interface TransportRepository {

    fun getAllVehicles(
            mapScope: MapScope,
            types: List<String>
    ): Observable<List<Vehicle>>

}