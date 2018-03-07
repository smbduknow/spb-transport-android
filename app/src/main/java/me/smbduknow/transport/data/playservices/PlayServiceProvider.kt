package me.smbduknow.transport.data.playservices

import android.content.Context
import me.smbduknow.transport.data.playservices.location.LocationProvider
import javax.inject.Inject


class PlayServiceProvider @Inject constructor(
        private val context: Context
) {

    fun getLastLocation() = LocationProvider.createLastLocationObservable(context)

}