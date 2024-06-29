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

import com.github.mikephil.charting.BuildConfig;

import org.osmdroid.api.IMapController;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    LocationManager locationManager;
    private IMapController mapController;
    EditText location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //onCreate, context and config loaded (think there is a problem with config function)
        //set content view as well, needs further explanation

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        setContentView(R.layout.activity_map);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx)) ;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //This method initializes the map

        startMap();

        //This function is used to create the on screen route

        DisplayRoute(51.4489, -0.9502);

        //location = findViewById(R.id.Search);

        Button button = (Button) findViewById(R.id.LocBtn);
        button.setOnClickListener((view) -> {

            //addMarker(51.4551,-0.9787, "Test Marker");
            //addMarker(51.4489, -0.9502, "Test2");
            for(int i = 1; i < 100; i++) {
                crimeMarker(i);
            }

        });
    }

    public void DisplayRoute(double longitude, double latitude) {
        MapRoute mapRoute = new MapRoute(map, this);
        mapRoute.addRoute(longitude, latitude);
    }

    //Testing method
    public void crimeMarker(int lineNum) {
        Scanner crimeDataIs = new Scanner(getResources().openRawResource(R.raw.thames_valley_street));
        String CDdata = "";

        CrimeData data = new CrimeData();

        crimeDataIs.nextLine();
        int count = 0;
        while(count != lineNum) {
            CDdata = crimeDataIs.nextLine();
            count++;
        }
        //CDdata = input.nextLine();
        double llong = CrimeData.returnCoordinates(CDdata, "Longitude");
        double latt = CrimeData.returnCoordinates(CDdata, "Latitude");
        String crime = CrimeData.returnCrime(CDdata);

        if(llong < 51.46 || llong > 51.45) {
            if(latt < -0.96 || latt > -0.98) {
                addMarker(latt,llong, crime);
            }
        }


    }

    public void addMarker(double latitude, double longitude, String markerName) {

        //Create new marker using osmdroid Marker method

        Marker startMarker = new Marker(map);

        //set GeoPoint location for marker

        GeoPoint startPoint = new GeoPoint(latitude,longitude);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        //use marker bitmap icon and set name of marker

        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_icon));
        startMarker.setTitle(markerName);

        //add marker to map

        map.getOverlays().add(startMarker);

        map.invalidate();
    }

    public void startMap() {

        //create new map and map controller using OSM and set the zoom

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(15);

        //create new geo point and set the center where the map initially loads into

        GeoPoint startPoint = new GeoPoint(51.4551,-0.9787); //GPS Tracker
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
}
