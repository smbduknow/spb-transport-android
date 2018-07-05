package me.smbduknow.transport.data

import com.google.transit.realtime.GtfsRealtime
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import me.smbduknow.transport.data.assets.RoutesProvider
import me.smbduknow.transport.data.network.VehiclesApi
import me.smbduknow.transport.domain.model.MapScope
import me.smbduknow.transport.domain.model.Route
import me.smbduknow.transport.domain.model.Vehicle
import me.smbduknow.transport.domain.repository.TransportRepository
import java.util.*
import javax.inject.Inject

class TransportRepositoryImpl @Inject constructor(
        private val remote: VehiclesApi,
        private val routesProvider: RoutesProvider
) : TransportRepository {

    override fun getAllVehicles(mapScope: MapScope,
                                types: List<String>,
                                routeId: String?): Single<List<Vehicle>> {

        val box = with(mapScope) {
            String.format(Locale.US, "%.4f,%.4f,%.4f,%.4f", sw.lon, sw.lat, ne.lon, ne.lat)
        }
        val transports = types.joinToString(",")

        return remote.getVehicles(box, transports, routeId)
                .map { it.entityList }
                .flattenAsObservable { it }
                .flatMapMaybe { Maybe.just(it).zipWith(
                        routesProvider.getRoute(it.vehicle.trip.routeId)
                ) }
                .map { mapVehicle(it.first, it.second) }
                .toList()

    }

    override fun searchRoutes(label: String): Single<List<Route>> =
            routesProvider.searchRoutesByLabel(label)


    // TODO move to separate mapper class
    private fun mapVehicle(entity: GtfsRealtime.FeedEntity, route: Route): Vehicle {
        return Vehicle(
                id = entity.id,
                label = route.label,
                type = route.typeLabel,
                latitude = entity.vehicle.position.latitude.toDouble(),
                longitude = entity.vehicle.position.longitude.toDouble(),
                bearing = entity.vehicle.position.bearing,
                routeId = route.id
        )
    }

}