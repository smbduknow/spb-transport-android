package me.smbduknow.transport.domain

import io.reactivex.Single
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Vehicle

interface MapInteractor {

    fun setMapScope(scope: MapScope)
    fun setVehicleTypes(types: List<String>)

    fun getVehicles(): Single<List<Vehicle>>
    fun getUserLocation(): Single<Coordinates>
}