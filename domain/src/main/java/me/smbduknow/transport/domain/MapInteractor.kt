package me.smbduknow.transport.domain

import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Vehicle
import rx.Observable

interface MapInteractor {

    fun setMapScope(scope: MapScope)
    fun setVehicleTypes(types: List<String>)

    fun getVehicles(): Observable<List<Vehicle>>
    fun getUserLocation(): Observable<Coordinates>
}