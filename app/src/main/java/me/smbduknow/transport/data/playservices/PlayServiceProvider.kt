package me.smbduknow.transport.data.playservices

import android.content.Context
import me.smbduknow.transport.data.playservices.location.LocationProvider
import java.lang.ref.WeakReference


object PlayServiceProvider {

    private lateinit var ctxRef: WeakReference<Context>

    fun init(ctx: Context) {
        ctxRef = WeakReference(ctx)
    }


    fun getLastLocation() = LocationProvider.createLastLocationObservable(ctxRef.get()!!)

}