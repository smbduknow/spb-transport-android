package me.smbduknow.transport;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;

    private Route searchRoute = new Route();
    private List<Route> routes;

    protected Map<String, Marker> markers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        routes = CSVUtil.readCsv(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Spb and move the camera
        LatLng spb = new LatLng(59.845, 30.325);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spb, 13.5f));
        mMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
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
        new BusTask(this, mMap, coordsString).execute();
    }



    public  class BusTask extends AsyncTask<Void, Void, Void> {

        private Activity activity;
        private GoogleMap map;
        private String coordsString;

        public BusTask(Activity activity, GoogleMap map, String coordsString) {
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
                        if(!markers.containsKey(entity.getId())) {
                            String label = findRouteLabel(routeId);
                            BitmapDrawable bd = DrawableUtil.writeOnDrawable(getApplicationContext(), R.drawable.ic_bus, label, 90+bearing);
                            BitmapDescriptor btmp = BitmapDescriptorFactory.fromBitmap(bd.getBitmap());
                            Marker marker = map.addMarker(new MarkerOptions().position(pos).icon(btmp).anchor(0.5f,0.5f));
                            markers.put(entity.getId(), marker);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String findRouteLabel(String routeId) {
        searchRoute.id = routeId;
        int pos = Collections.binarySearch(routes, searchRoute);
        return pos >=0 ? routes.get(pos).label : "";
    }
}
