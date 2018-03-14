package me.smbduknow.transport.domain.interactor

import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Vehicle
import me.smbduknow.transport.domain.repository.TransportRepository
import me.smbduknow.transport.domain.repository.UserLocationRepository
import javax.inject.Inject

class MapInteractorImpl @Inject constructor(
        private val transportRepository: TransportRepository,
        private val userLocationRepository: UserLocationRepository
) : MapInteractor {

    private var scope = MapScope(
            Coordinates(0.0, 0.0),
            Coordinates(0.0, 0.0),
            Coordinates(0.0, 0.0),
            0f,
            1f
    )
    private var vehicleTypes: List<String>
            = listOf(Vehicle.TYPE_BUS, Vehicle.TYPE_TRAM, Vehicle.TYPE_TROLLEY)


    override fun setMapScope(scope: MapScope) {
        this.scope = scope
    }

    override fun setVehicleTypes(types: List<String>) {
        vehicleTypes = types
    }


    override fun getVehicles() = transportRepository.getAllVehicles(scope, vehicleTypes)

    override fun getUserLocation() = userLocationRepository.getUserLocation()

}