package me.smbduknow.transport.domain

import io.reactivex.Single
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.Vehicle

interface MapInteractor {

    fun setBounds(sw: Coordinates, ne: Coordinates)
    fun setVehicleTypes(types: List<String>)
    fun setSelectedRoute(id: String)

    fun getVehicles(): Single<List<Vehicle>>
    fun getUserLocation(): Single<Coordinates>
}