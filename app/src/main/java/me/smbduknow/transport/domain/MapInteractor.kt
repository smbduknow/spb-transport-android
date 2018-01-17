package me.smbduknow.transport.domain

import com.google.transit.realtime.GtfsRealtime
import me.smbduknow.transport.data.playservices.PlayServiceProvider
import me.smbduknow.transport.data.session.Session
import me.smbduknow.transport.model.Route
import me.smbduknow.transport.model.Vehicle
import rx.Observable
import timber.log.Timber
import java.net.URL
import java.util.*

class MapInteractor {

    private var routes = Session.routes

    fun getVehicles(types: List<String>, box: String): Observable<List<Vehicle>> {

        val transports = types.joinToString(",")
        val url = URL("http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle?" +
                "bbox=$box&transports=$transports")
        Timber.d("transport", url.toString())

        return Observable.fromCallable { requestVehicles(url) }
                .map { it.entityList.map {
                    val route = findRoute(it.vehicle.trip.routeId)
                    Vehicle(
                            id = it.id,
                            label = route?.label ?: "",
                            type = route?.typeLabel ?: "",
                            latitude = it.vehicle.position.latitude.toDouble(),
                            longitude = it.vehicle.position.longitude.toDouble(),
                            bearing = it.vehicle.position.bearing
                    )
                } }
    }

    fun getUserLocation(): Observable<Pair<Double, Double>> {
        return PlayServiceProvider.getLastLocation()
                .map { Pair(it.latitude, it.longitude) }
    }

    private fun requestVehicles(url: URL) = url.openStream().use { GtfsRealtime.FeedMessage.parseFrom(it) }

    private fun findRoute(routeId: String): Route? {
        val pos = Collections.binarySearch(routes, Route(id = routeId))
        return if (pos >= 0) routes[pos] else null
    }

}