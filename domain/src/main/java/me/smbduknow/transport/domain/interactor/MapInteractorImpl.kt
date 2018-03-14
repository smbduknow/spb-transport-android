package me.smbduknow.transport.domain.interactor

import me.smbduknow.transport.domain.IMapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Vehicle
import me.smbduknow.transport.domain.repository.TransportRepository
import me.smbduknow.transport.domain.repository.UserLocationRepository
import javax.inject.Inject

class MapInteractorImpl @Inject constructor(
        private val transportRepository: TransportRepository,
        private val userLocationRepository: UserLocationRepository
) : IMapInteractor {

    private var bounds = MapScope(
            Coordinates(0.0, 0.0),
            Coordinates(0.0, 0.0)
    )
    private var vehicleTypes: List<String>
            = listOf(Vehicle.TYPE_BUS, Vehicle.TYPE_TRAM, Vehicle.TYPE_TROLLEY)


    override fun setBounds(sw: Coordinates, ne: Coordinates) {
        bounds = MapScope(sw, ne)
    }

    fun setVehicleTypes(types: List<String>) {
        vehicleTypes = types
    }


    override fun getVehicles() = transportRepository.getAllVehicles(bounds, vehicleTypes)

    fun getUserLocation() = userLocationRepository.getUserLocation()

}