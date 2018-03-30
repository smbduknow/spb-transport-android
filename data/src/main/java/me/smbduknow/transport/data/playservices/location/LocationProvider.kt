package me.smbduknow.transport.data.playservices.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import me.smbduknow.transport.data.playservices.PlayServiceObservable


class LocationProvider(context: Context)
    : PlayServiceObservable<Location>(context, LocationServices.API) {

    companion object {
        fun createLastLocationObservable(context: Context): Single<Location> =
                Observable.create(LocationProvider(context)).singleOrError()
    }

    @SuppressLint("MissingPermission")
    override fun onApiClientReady(apiClient: GoogleApiClient, observer: ObservableEmitter<in Location>) {
        val location = LocationServices.FusedLocationApi.getLastLocation(apiClient)
        if (location != null) {
            observer.onNext(location)
        }
        observer.onComplete()
    }
}