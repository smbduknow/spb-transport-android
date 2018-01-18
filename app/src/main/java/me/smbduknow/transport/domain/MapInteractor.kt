package me.smbduknow.transport.domain

import com.google.transit.realtime.GtfsRealtime
import me.smbduknow.transport.data.network.VehiclesApi
import me.smbduknow.transport.data.playservices.PlayServiceProvider
import me.smbduknow.transport.data.session.Session
import me.smbduknow.transport.model.MapBounds
import me.smbduknow.transport.model.Route
import me.smbduknow.transport.model.Vehicle
import rx.Observable
import java.util.*

class MapInteractor {

    private val api = VehiclesApi()
    private val routes = Session.routes

    private var bounds = MapBounds(0.0, 0.0, 0.0, 0.0)
    private var vehicleTypes: List<String> = listOf("bus","trolley","tram")

    fun setBounds(swLat:Double, swLon: Double,
                  neLat:Double, neLon: Double) {
        bounds = MapBounds(swLat, swLon, neLat, neLon)
    }

    fun setVehicleTypes(types: List<String>) {
        vehicleTypes = types
    }

    fun getVehicles(): Observable<List<Vehicle>> {
        return api.getVehicles(
                bounds.swLat, bounds.swLon,
                bounds.neLat, bounds.neLon,
                vehicleTypes)
                .map { it.entityList.map { mapVehicle(it) } }
    }

    fun getUserLocation(): Observable<Pair<Double, Double>> {
        return PlayServiceProvider.getLastLocation()
                .map { Pair(it.latitude, it.longitude) }
    }


    private fun mapVehicle(entity: GtfsRealtime.FeedEntity): Vehicle {
        val route = findRoute(entity.vehicle.trip.routeId)
        return Vehicle(
                id = entity.id,
                label = route?.label ?: "",
                type = route?.typeLabel ?: "",
                latitude = entity.vehicle.position.latitude.toDouble(),
                longitude = entity.vehicle.position.longitude.toDouble(),
                bearing = entity.vehicle.position.bearing
        )
    }

    private fun findRoute(routeId: String): Route? {
        val pos = Collections.binarySearch(routes, Route(id = routeId))
        return if (pos >= 0) routes[pos] else null
    }

}