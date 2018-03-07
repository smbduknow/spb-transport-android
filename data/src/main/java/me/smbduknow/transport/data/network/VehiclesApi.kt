package me.smbduknow.transport.data.network

import android.util.Log
import com.google.transit.realtime.GtfsRealtime
import rx.Observable
import java.net.URL
import javax.inject.Inject


class VehiclesApi @Inject constructor() {

    private val VEHICLE_URL = "http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle"

    fun getVehicles(box: String, transports: String): Observable<GtfsRealtime.FeedMessage> {

        val url = URL("$VEHICLE_URL?bbox=$box&transports=$transports")
        Log.d("VehiclesApi", url.toString())

        return Observable.fromCallable {
            url.openStream().use {
                GtfsRealtime.FeedMessage.parseFrom(it)
            }
        }
    }

}