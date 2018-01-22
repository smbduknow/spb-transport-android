package me.smbduknow.transport.data

import com.google.transit.realtime.GtfsRealtime
import me.smbduknow.transport.data.network.VehiclesApi
import me.smbduknow.transport.data.session.Session
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Route
import me.smbduknow.transport.domain.model.Vehicle
import me.smbduknow.transport.domain.repository.TransportRepository
import rx.Observable
import java.util.*

class TransportRepositoryImpl : TransportRepository {

    private val api = VehiclesApi()
    private val session = Session

    override fun getAllVehicles(mapScope: MapScope, types: List<String>): Observable<List<Vehicle>> {

        val box = with(mapScope) {
            String.format(Locale.US, "%.4f,%.4f,%.4f,%.4f", sw.lon, sw.lat, ne.lon, ne.lat)
        }
        val transports = types.joinToString(",")

        return api.getVehicles(box, transports)
                .map { it.entityList.map { mapVehicle(it) } }
    }


    // TODO move to separate mapper class
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
        val pos = Collections.binarySearch(session.routes, Route(id = routeId))
        return if (pos >= 0) session.routes[pos] else null
    }

}