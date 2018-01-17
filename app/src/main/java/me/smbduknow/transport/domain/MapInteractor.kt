package me.smbduknow.transport.domain

import android.util.Log
import com.google.transit.realtime.GtfsRealtime
import me.smbduknow.transport.data.Session
import me.smbduknow.transport.model.Route
import me.smbduknow.transport.model.Vehicle
import rx.Observable
import java.net.URL
import java.util.*

class MapInteractor {

    private var routes = Session.routes

    fun getVehicles(types: List<String>, box: String): Observable<List<Vehicle>> {

        val transports = types.joinToString(",")
        val url = URL("http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle?" +
                "bbox=$box&transports=$transports")
        Log.d("transport", url.toString())

        return Observable.fromCallable { url.openStream().use { GtfsRealtime.FeedMessage.parseFrom(it) } }
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

    private fun findRoute(routeId: String): Route? {
        val pos = Collections.binarySearch(routes, Route(id = routeId))
        return if (pos >= 0) routes[pos] else null
    }

}