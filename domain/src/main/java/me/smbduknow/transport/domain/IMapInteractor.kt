package me.smbduknow.transport.domain

import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.Vehicle
import rx.Observable

interface IMapInteractor {

    fun setBounds(sw: Coordinates, ne: Coordinates)

    fun getVehicles() : Observable<List<Vehicle>>
}