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

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        startMap();
        crimeTest(csvToString());
        //returnFileSize();
        //DisplayRoute(51.4489, -0.9502);

        location = findViewById(R.id.inputLoc);

        Button button = (Button) findViewById(R.id.LocBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addMarker(51.4551,-0.9787, "Test Marker");
                addMarker(51.4489, -0.9502, "Test2");
            }
        });
    }

    public void DisplayRoute(double longitude, double latitude) {
        MapRoute mapRoute = new MapRoute(map, this);
        mapRoute.addRoute(longitude, latitude);
    }

    public String csvToString() {
        String str = "";
        StringBuffer buffer = new StringBuffer();
        InputStream is = getResources().openRawResource(R.raw.thames_valley_street);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buffer.append(str + "\n");
                }
            }
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } {

        }
        return buffer.toString();
    }

    //Testing method
    public void returnFileSize() {
        TextView test = (TextView) findViewById(R.id.textView);
        CrimeData data = new CrimeData();
        int fileSize = CrimeData.fileSize(csvToString());
        test.setText("Hello" + fileSize);
    }

    //testing method

    public void crimeTest(String inputData) {
        TextView test = (TextView) findViewById(R.id.textView);
        CrimeData data = new CrimeData();
        //double Longitude = CrimeData.returnLong(inputData, 2);
        //double Latitude = CrimeData.returnLat(inputData, 2);
        String Crime = CrimeData.returnCrime(inputData, "Crime", 2);
        test.setText("Data : " + Crime);
    }

    public void displayCrimeData(String inputData) {
        CrimeData crimeData = new CrimeData();
        for(int i = 0; i < CrimeData.fileSize(inputData); i++) {
            double Longitude = CrimeData.returnLong(inputData, "Longitude", i);
            double Latitude = CrimeData.returnLat(inputData, "Latitude", i);
            String Crime = CrimeData.returnCrime(inputData, "Crime", i);
            //addMarker(Longitude, Latitude, Crime);
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
