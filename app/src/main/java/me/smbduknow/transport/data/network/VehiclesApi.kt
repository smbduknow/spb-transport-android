package me.smbduknow.transport.data.network

import com.google.transit.realtime.GtfsRealtime
import rx.Observable
import timber.log.Timber
import java.net.URL
import java.util.*

class VehiclesApi {

    private val VEHICLE_URL = "http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle"

    fun getVehicles(
            swLat:Double, swLon: Double,
            neLat:Double, neLon: Double,
            types: List<String>): Observable<GtfsRealtime.FeedMessage> {

        val box = String.format(Locale.US, "%.4f,%.4f,%.4f,%.4f", swLon, swLat, neLon, neLat)
        val transports = types.joinToString(",")

        val url = URL("$VEHICLE_URL?bbox=$box&transports=$transports")
        Timber.d(url.toString())

        return Observable.fromCallable { requestVehicles(url) }
    }

    private fun requestVehicles(url: URL)
            = url.openStream().use { GtfsRealtime.FeedMessage.parseFrom(it) }

}