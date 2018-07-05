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

    private var bounds = MapScope(
            Coordinates(0.0, 0.0),
            Coordinates(0.0, 0.0)
    )
    private var vehicleTypes: List<String>
            = listOf(Vehicle.TYPE_BUS, Vehicle.TYPE_TRAM, Vehicle.TYPE_TROLLEY)

    private var selectedRouteId: String? = null

    override fun setBounds(sw: Coordinates, ne: Coordinates) {
        bounds = MapScope(sw, ne)
    }

    override fun setVehicleTypes(types: List<String>) {
        vehicleTypes = types
    }

    override fun setSelectedRoute(id: String) {
        selectedRouteId = id
    }


    override fun getVehicles() =
            transportRepository.getAllVehicles(bounds, vehicleTypes, selectedRouteId)

    override fun getUserLocation() =
            userLocationRepository.getUserLocation()


    override fun searchRoutes(query: String) =
            transportRepository.searchRoutes(query)
}