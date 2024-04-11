package com.example.osmtest2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    LocationManager locationManager;

    private IMapController mapController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //onCreate, context and config loaded (think there is a problem with config function)
        //set content view as well, needs further explanation

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        setContentView(R.layout.activity_map);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //new GPS tracker from GPSTracker class and retrieving latitude and longitude (doesn't work yet, need to change variables)

        GPSTracker GPSLoc = new GPSTracker();
        String latitude = String.valueOf(GPSLoc.getD_lat());
        String longitude = String.valueOf(GPSLoc.getD_long());

        //displaying latitude and longitude on screen (can enter own values as well) not working yet
        //could or should be put in separate method, or class

        final TextView helloTextView = (TextView) findViewById(R.id.textView6);
        helloTextView.setText(latitude);
        final TextView Text2 = (TextView) findViewById(R.id.textView7);
        Text2.setText(longitude);

        //final EditText lattitude = (EditText) findViewById(R.id.lattitude);
        //int latt = Integer.parseInt(lattitude.getText().toString());
        //final EditText longtitude = (EditText) findViewById(R.id.longtitude);
        //int llong = Integer.parseInt(longtitude.getText().toString());

        Button coordinates = (Button) findViewById(R.id.button);

        coordinates.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateMap();
            }
        });

        //create new map and map controller using OSM and set the zoom

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(10);

        //setup location manager, should be used for

        locationManager = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastLocation != null) {
            updateLoc(lastLocation);
        }

        //create a new marker to add to map, not working

        Marker m = new Marker(map);

        try {
            JSONArray markers = new JSONArray();
            addMarkers(markers, m);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            map.invalidate();
            mapController.setCenter(m.getPosition());
        }

        //create new geo point and set the center where the map initially loads into

        //GeoPoint startPoint = new GeoPoint(GPSLoc.getD_lat(),GPSLoc.getD_long());
        GeoPoint startPoint = new GeoPoint(10,-50);
        mapController.setCenter(startPoint);


    }

    public void updateMap() {
        GeoPoint Reading = new GeoPoint(51.4551, 0.9787);
        mapController.animateTo(Reading);
        mapController.setZoom(20);
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void addMarkers(JSONArray markers, Marker m) throws JSONException {
        for (int i = 0; i < markers.length(); i++) {
            JSONObject marker = markers.getJSONObject(i);
            m.setPosition(new GeoPoint(marker.getDouble("lat"), marker.getDouble("lon")));
            m.setTitle(marker.getString("data") + " @ " + marker.getString("date"));
            m.setIcon(getResources().getDrawable(R.drawable.waypoint));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
            map.getOverlays().add(m);
        }
    }


    public void onLocationChanged(@NonNull Location location) {

    }

    //updates location and re sets the center

    public void updateLoc(Location loc) {
        GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        mapController.setCenter(locGeoPoint);
        map.invalidate();
    }


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            updateLoc(location);
        }
    };
}
