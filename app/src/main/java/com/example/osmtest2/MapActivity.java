package com.example.osmtest2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Geocoder.GeocodeListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    LocationManager locationManager;
    private IMapController mapController;
    EditText location;
    TextView latitude;
    TextView longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //onCreate, context and config loaded (think there is a problem with config function)
        //set content view as well, needs further explanation

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        setContentView(R.layout.activity_map);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        startMap();

        location = findViewById(R.id.inputLoc);
        latitude = findViewById(R.id.latt);
        longitude = findViewById(R.id.longg);

        Button button = (Button) findViewById(R.id.LocBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonGetCoordinates(map);
            }
        });
    }

    public void buttonGetCoordinates(MapView view) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(location.toString(), 1);

            if (addressList != null) {
                double doubleLat = addressList.get(0).getLatitude();
                double doublelong = addressList.get(0).getLongitude();

                latitude.setText("Latitude: " + String.valueOf(doubleLat));
                longitude.setText("Longitude: " + String.valueOf(doublelong));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startMap() {
        //create new map and map controller using OSM and set the zoom

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(10);

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
