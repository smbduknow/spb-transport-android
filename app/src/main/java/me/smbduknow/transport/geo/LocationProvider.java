package me.smbduknow.transport.geo;

import android.location.Location;

public interface LocationProvider {

    interface OnRecieveLocationListener {
        void onProviderConnected();
        void onReceiveLocation(Location location);
        void onReceiveFailed();
    }

    boolean isAvailable();

    void requestLastLocation();
    void requestLocationUpdates();
    void removeLocationUpdates();

//    void release();
}
