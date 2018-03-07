package me.smbduknow.transport.data.playservices.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import me.smbduknow.transport.data.playservices.PlayServiceObservable
import rx.Observable
import rx.Observer


class LocationProvider(context: Context)
    : PlayServiceObservable<Location>(context, LocationServices.API) {

    companion object {
        fun createLastLocationObservable(context: Context): Observable<Location> {
            return Observable.unsafeCreate(LocationProvider(context))
        }
    }

    @SuppressLint("MissingPermission")
    override fun onApiClientReady(apiClient: GoogleApiClient, observer: Observer<in Location>) {
        val location = LocationServices.FusedLocationApi.getLastLocation(apiClient)
        if (location != null) {
            observer.onNext(location)
        }
        observer.onCompleted()
    }
}