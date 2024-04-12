package com.example.osmtest2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    LocationManager locationManager;
    private IMapController mapController;
    EditText location;
    TextView data;
    TextView longitude;
    private static final String USER_AGENT = "Reading";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //onCreate, context and config loaded (think there is a problem with config function)
        //set content view as well, needs further explanation

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        setContentView(R.layout.activity_map);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        startMap();
        route();

        location = findViewById(R.id.inputLoc);
        data = findViewById(R.id.data);
        //longitude = findViewById(R.id.longg);

        Button button = (Button) findViewById(R.id.LocBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createMarker(51.4551,-0.9787);
                createMarker(51.5072, -0.1276);
            }
        });
    }

    public void displayCrimeData(TextView data) {
        CrimeData crimeData = new CrimeData();
        data.setText(crimeData.readRecord("Reading",
                "R.raw.thames_valley_street.csv"));

    }

    public void createMarker(double latitude, double longitude) {
        Marker startMarker = new Marker(map);
        GeoPoint startPoint = new GeoPoint(latitude,longitude);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_icon));
        startMarker.setTitle("Start Point");

        map.getOverlays().add(startMarker);

        map.invalidate();
    }

    public void route() {
        RoadManager roadManager = new OSRMRoadManager(this, USER_AGENT);
        ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_BIKE);

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        GeoPoint startPoint = new GeoPoint(51.4551, -0.9787);
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(51.5072, -0.1276);
        waypoints.add(endPoint);

        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }

    public void startMap() {
        //create new map and map controller using OSM and set the zoom

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(15);

        //create new geo point and set the center where the map initially loads into

        //GeoPoint startPoint = new GeoPoint(GPSLoc.getD_lat(),GPSLoc.getD_long());
        GeoPoint startPoint = new GeoPoint(51.4551,-0.9787);
        mapController.setCenter(startPoint);
    }

    public void updateMap() {
        GeoPoint Reading = new GeoPoint(51.4551, -0.9787);
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
