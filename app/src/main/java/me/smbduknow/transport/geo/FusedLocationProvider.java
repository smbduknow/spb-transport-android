package me.smbduknow.transport.geo;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class FusedLocationProvider implements
        LocationProvider,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final GoogleApiClient locationClient;
    private LocationManager locationManager;
    private OnRecieveLocationListener mListener;

    public FusedLocationProvider(Context context, OnRecieveLocationListener listener) {
        mListener = listener;
        locationClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationClient.connect();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean isAvailable() {
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public void requestLastLocation() {
        try {
            if (locationClient != null && locationClient.isConnected() && mListener != null)
                mListener.onReceiveLocation(LocationServices.FusedLocationApi.getLastLocation(locationClient));
        } catch (Exception ignored) {}
    }

    @Override
    public void requestLocationUpdates() {
        if(locationClient.isConnected()) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setSmallestDisplacement(10);
            locationRequest.setFastestInterval(5000);
            locationRequest.setInterval(15000);
            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
        }
    }

    @Override
    public void removeLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);
        } catch (Exception ignored) {}
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            if(locationClient != null && locationClient.isConnected() && mListener != null)
                mListener.onProviderConnected();
        } catch (Exception ignored) {}
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mListener.onReceiveFailed();
    }

    @Override
    public void onLocationChanged(Location location) {
        mListener.onReceiveLocation(location);
    }

}