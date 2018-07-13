package me.smbduknow.transport.data.network

import android.util.Log
import com.google.transit.realtime.GtfsRealtime
import io.reactivex.Single
import java.net.URL
import javax.inject.Inject


class VehiclesApi @Inject constructor() {

    private val VEHICLE_URL = "http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle"

    fun getVehicles(box: String, transports: String, routeIds: List<String>): Single<GtfsRealtime.FeedMessage> {

        val routeParam = if(routeIds.isNotEmpty()) "&routeIDs=${routeIds.joinToString(",")}" else ""
        val url = URL("$VEHICLE_URL?bbox=$box&transports=$transports$routeParam")
        Log.d("VehiclesApi", url.toString())

        return Single.fromCallable {
            url.openStream().use {
                GtfsRealtime.FeedMessage.parseFrom(it)
            }
        }
    }

}