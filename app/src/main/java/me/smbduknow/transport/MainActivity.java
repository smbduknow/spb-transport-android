package me.smbduknow.transport;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.transit.realtime.GtfsRealtime;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.clear();

        LatLngBounds cameraBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        String coordsString = String.format(Locale.US, "%.2f,%.2f,%.2f,%.2f",
                cameraBounds.southwest.longitude, cameraBounds.southwest.latitude,
                cameraBounds.northeast.longitude, cameraBounds.northeast.latitude
        );
        new BusTask(this, mMap, coordsString).execute();
    }



    public static class BusTask extends AsyncTask<Void, Void, Void> {

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
                    final String label = entity.getVehicle().getVehicle().getLabel();
                    final LatLng pos = new LatLng(entity.getVehicle().getPosition().getLatitude(), entity.getVehicle().getPosition().getLongitude());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            map.addMarker(new MarkerOptions().position(pos).title(label));
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
