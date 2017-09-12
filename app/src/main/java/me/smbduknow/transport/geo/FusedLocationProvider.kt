package me.smbduknow.transport.geo

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class FusedLocationProvider(context: Context, private val mListener: LocationProvider.OnRecieveLocationListener?) : LocationProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private val locationClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    private val locationManager: LocationManager

    init {
        locationClient.connect()

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override val isAvailable: Boolean
        get() = try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ignored: Exception) {
            false
        }


    override fun requestLastLocation() {
        try {
            if (locationClient != null && locationClient.isConnected && mListener != null)
                mListener.onReceiveLocation(LocationServices.FusedLocationApi.getLastLocation(locationClient))
        } catch (ignored: Exception) {
        }

    }

    override fun requestLocationUpdates() {
        if (locationClient!!.isConnected) {
            val locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.smallestDisplacement = 10f
            locationRequest.fastestInterval = 5000
            locationRequest.interval = 15000
            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this)
        }
    }

    override fun removeLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this)
        } catch (ignored: Exception) {
        }

    }

    override fun onConnected(bundle: Bundle?) {
        try {
            if (locationClient != null && locationClient.isConnected && mListener != null)
                mListener.onProviderConnected()
        } catch (ignored: Exception) {
        }

    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        mListener!!.onReceiveFailed()
    }

    override fun onLocationChanged(location: Location) {
        mListener!!.onReceiveLocation(location)
    }

}