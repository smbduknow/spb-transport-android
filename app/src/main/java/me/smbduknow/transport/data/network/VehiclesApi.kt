package me.smbduknow.transport.data.network

import com.google.transit.realtime.GtfsRealtime
import rx.Observable
import timber.log.Timber
import java.net.URL

class VehiclesApi {

    private val VEHICLE_URL = "http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle"

    fun getVehicles(box: String, transports: String): Observable<GtfsRealtime.FeedMessage> {

        val url = URL("$VEHICLE_URL?bbox=$box&transports=$transports")
        Timber.d(url.toString())

        return Observable.fromCallable {
            url.openStream().use {
                GtfsRealtime.FeedMessage.parseFrom(it)
            }
        }
    }

}