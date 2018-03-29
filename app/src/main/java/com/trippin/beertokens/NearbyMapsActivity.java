package com.trippin.beertokens;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trippin.beertokens.tasks.SearchNearbyPubsTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NearbyMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private static final int RADIUS = 10000;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 101;

    private static LatLng currentCameraPosition;
    private static LatLng currentPosition;

    private final Map<String, Place> markerMap = new HashMap<>();
    private GoogleMap map;
    private String pubTopName;
    private Timer mapSettledTimer; // In the case of lots of drags, wait a bit before searching for pubs

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get the pub name from the parameters passed in on the intent
        Bundle extras = getIntent().getExtras();
        pubTopName = extras == null ? "" : extras.getString("pubName");

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(pubTopName);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        // Attempt to move to current location
        moveCameraToCurrentLocation();

        // Then load the pubs
        showLocalPubs();

        //  Register idle listener so we can reload the pubs if user has moved the map
        map.setOnCameraIdleListener(this);
    }

    private void showLocalPubs() {

        // Task searches for nearby pubs and display markers
        new SearchNearbyPubsTask(
            pubTopName,
            currentCameraPosition.latitude,
            currentCameraPosition.longitude,
            RADIUS,
            map,
            markerMap,
            new PostPubSearch()
        ).execute();
    }

    private boolean moveCameraToCurrentLocation() {

        try {
            if (currentPosition == null) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    return false;
                }

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location == null)
                    currentPosition = new LatLng(52.446225, -2.0351);
                else
                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                currentCameraPosition = currentPosition;
            }

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentCameraPosition, 10);
            map.moveCamera(cameraUpdate);

            return true;

        } catch (Exception ex) {
            Toast.makeText(this, "Fucked: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("BeerTokens", "Android dev is shit", ex);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Toast.makeText(this, "DONT HAVE GPS access, requesting", Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showLocalPubs();
                } else {
                    Toast.makeText(this, "Need GPS access to continue", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onCameraIdle() {

        // Get the new position from the map
        currentCameraPosition = map.getCameraPosition().target;

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(currentCameraPosition);
        circleOptions.strokeWidth(2);
        circleOptions.radius(RADIUS);
        circleOptions.strokeColor(Color.BLACK);
        map.addCircle(circleOptions);

        // Cancel old task
        if (mapSettledTimer != null)
            mapSettledTimer.cancel();

        // Wait a second before loading pubs
        mapSettledTimer = new Timer();
        mapSettledTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                showLocalPubs();
            }}, 1000);
    }

    public class PostPubSearch {

        public void run(List<Place> pubs) {

            Toast.makeText(NearbyMapsActivity.this, "Found " + pubs.size() + " pubs", Toast.LENGTH_SHORT).show();

            map.clear();

            for (Place pub : pubs) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(pub.getLatLng())
                        .title(pub.getName().toString());
                Marker marker = map.addMarker(markerOptions);
                markerMap.put(marker.getId(), pub);
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                    final SearchNearbyPubsTask.PlaceImpl pub = (SearchNearbyPubsTask.PlaceImpl) markerMap.get(marker.getId());

                    Intent intent = new Intent(NearbyMapsActivity.this, PubActivity.class);
                    intent.putExtra("pubId", pub.getId());
                    intent.putExtra("pubPlaceId", pub.getPlaceId());
                    intent.putExtra("pubName", pub.getName());
                    intent.putExtra("pubTopName", pubTopName);
                    intent.putExtra("pubAddress", pub.getAddress());
                    intent.putExtra("pubPhoneNumber", pub.getPhoneNumber());
                    intent.putExtra("pubRating", pub.getRating());
                    intent.putExtra("pubWebsiteUrl", pub.getWebsiteUri());
                    intent.putExtra("pubLatitude", pub.getLatLng().latitude);
                    intent.putExtra("pubLongitude", pub.getLatLng().longitude);
                    intent.putExtra("currentLatitude", currentPosition.latitude);
                    intent.putExtra("currentLongitude", currentPosition.longitude);
                    startActivity(intent);

                    return true;
                 }
            });
                }
            });
        }
    }
}
