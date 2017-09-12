package me.smbduknow.transport.geo

import android.location.Location

interface LocationProvider {

    interface OnRecieveLocationListener {
        fun onProviderConnected()
        fun onReceiveLocation(location: Location)
        fun onReceiveFailed()
    }

    val isAvailable: Boolean

    fun requestLastLocation()
    fun requestLocationUpdates()
    fun removeLocationUpdates()

    //    void release();
}
