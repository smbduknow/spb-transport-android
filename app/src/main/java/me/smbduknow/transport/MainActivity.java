package me.smbduknow.transport;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.transit.realtime.GtfsRealtime;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.smbduknow.transport.commons.CSVUtil;
import me.smbduknow.transport.commons.DrawableUtil;
import me.smbduknow.transport.geo.FusedLocationProvider;
import me.smbduknow.transport.geo.LocationProvider;
import me.smbduknow.transport.model.Route;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;

    private Route searchRoute = new Route();
    private List<Route> routes;

    protected Map<String, Marker> markers = new HashMap<>();

    private LocationProvider locationProvider;

    private long millis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        routes = CSVUtil.readCsv(this);

        locationProvider = new FusedLocationProvider(this, locationListener);

        millis = System.currentTimeMillis();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng spb = new LatLng(59.845, 30.325);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spb, 13.5f));
        mMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(System.currentTimeMillis() < millis + 1000) return;

        LatLngBounds cameraBounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        for(Iterator<Map.Entry<String, Marker>> it = markers.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Marker> entry = it.next();
            Marker marker = entry.getValue();
            if(!cameraBounds.contains(marker.getPosition())) {
                marker.remove();
                it.remove();
            }
        }

        String coordsString = String.format(Locale.US, "%.4f,%.4f,%.4f,%.4f",
                cameraBounds.southwest.longitude, cameraBounds.southwest.latitude,
                cameraBounds.northeast.longitude, cameraBounds.northeast.latitude
        );

        new TransportTask(this, mMap, coordsString).execute();
    }



    public  class TransportTask extends AsyncTask<Void, Void, Void> {

        private Activity activity;
        private GoogleMap map;
        private String coordsString;

        public TransportTask(Activity activity, GoogleMap map, String coordsString) {
            this.activity = activity;
            this.map = map;
            this.coordsString = coordsString;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://transport.orgp.spb.ru/Portal/transport/internalapi/gtfs/realtime/vehicle?" +
                        "bbox="+coordsString+"&transports=bus,trolley,tram,ship");
                Log.d("transport", url.toString());
                GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(url.openStream());
                for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                    final float bearing = entity.getVehicle().getPosition().getBearing();
                    final String routeId = entity.getVehicle().getTrip().getRouteId();
                    final LatLng pos = new LatLng(entity.getVehicle().getPosition().getLatitude(), entity.getVehicle().getPosition().getLongitude());
                    activity.runOnUiThread(() -> {
                        Route route = findRoute(routeId);
                        int res = R.drawable.ic_bus;
                        if(route.typeLabel.equals("tram")) res = R.drawable.ic_tram;
                        if(route.typeLabel.equals("trolley")) res = R.drawable.ic_troll;
                        BitmapDrawable bd = DrawableUtil.writeOnDrawable(getApplicationContext(), res, route.label, -90+bearing);
                        BitmapDescriptor btmp = BitmapDescriptorFactory.fromBitmap(bd.getBitmap());
                        if(!markers.containsKey(entity.getId())) {
                            Marker marker = map.addMarker(new MarkerOptions().position(pos).icon(btmp).anchor(0.5f,0.5f));
                            markers.put(entity.getId(), marker);
                        } else {
                            Marker marker = markers.get(entity.getId());
                            marker.setPosition(pos);
                            marker.setIcon(btmp);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Route findRoute(String routeId) {
        searchRoute.id = routeId;
        int pos = Collections.binarySearch(routes, searchRoute);
        return pos >=0 ? routes.get(pos) : null;
    }



    protected void requestUserLocation() {
        checkPermission();
    }



    private LocationProvider.OnRecieveLocationListener locationListener = new LocationProvider.OnRecieveLocationListener() {
        @Override
        public void onProviderConnected() {
            requestUserLocation();
        }
        @Override
        public void onReceiveLocation(Location location) {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f));
            }
        }
        @Override
        public void onReceiveFailed() {

        }
    };



    private static final int REQUEST_CODE_PERMISSION = 1;

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionGranted();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length >= 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted();
            }
        }
    }

    private void permissionGranted() {
        if(locationProvider != null && locationProvider.isAvailable()) {
            locationProvider.requestLastLocation();
        }
    }
}
