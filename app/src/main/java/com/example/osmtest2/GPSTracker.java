package com.example.osmtest2;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.List;
import java.util.Locale;

public class GPSTracker extends MapActivity {
    //FusedLocationProviderAPI - Location API for android
    //CheckLocationPermission()
    //startLocationUpdates()
    //StopLocationUpdates()
    //recieveLocation()

    private FusedLocationProviderClient FLPC;
    private LocationCallback LC;
    private SettingsClient SC;
    private LocationRequest LR;
    private LocationSettingsRequest LSR;
    private Location L;

    Context context;
    double d_lat, d_long;

    String fetched_address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        context=getApplicationContext();
        checkLocationPermission();
        init();
    }

    private void checkLocationPermission() {
        Log.d(TAG,"inside check location");

        if(ContextCompat.checkSelfPermission(GPSTracker.this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
    }

    //@Override
    public void onLocationChanged(@NonNull Location location) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(GPSTracker.this,android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(GPSTracker.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            ActivityCompat.requestPermissions(GPSTracker.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(GPSTracker.this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted..",Toast.LENGTH_SHORT).show();
                        //init();  //code of after request for location is granted...
                    }
                }
                return;
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        SC.checkLocationSettings(LSR)
                .addOnSuccessListener(locationSettingsResponse -> {
                    Log.d(TAG,"Location settings are OK");
                    FLPC.requestLocationUpdates(LR,LC, Looper.myLooper());
                });
        //.addOnFailiureListner(e ->{
        //    int statusCode = ((ApiException) e).getStatusCode();
        //    Log.d(TAG,"inside error -> " + statusCode);
        //});
    }

    public void stopLocationUpdates() {
        FLPC
                .removeLocationUpdates(LC)
                .addOnCompleteListener(task -> {Log.d(TAG, "stop location updates");});
    }

    private void recieveLocation(LocationResult locationResult) {
        L = locationResult.getLastLocation();
        Log.d(TAG,"latitude : "+L.getLatitude());
        Log.d(TAG,"longitude : "+L.getLongitude());
        Log.d(TAG,"altitude : "+L.getAltitude());

        String s_lat=String.format(Locale.ROOT, "%.6f",L.getLatitude());
        String s_lon=String.format(Locale.ROOT, "%.6f",L.getLongitude());

        d_lat=L.getLatitude();
        d_long=L.getLongitude();

        try {
            Geocoder geocoder= new Geocoder(this, Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(d_lat,d_long,1);

            fetched_address=addresses.get(0).getAddressLine(0);
            Log.d(TAG,""+fetched_address);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getD_lat() {
        return d_lat;
    }

    public double getD_long() {
        return d_long;
    }

    public void init() {
        FLPC = LocationServices.getFusedLocationProviderClient(this);
        SC = LocationServices.getSettingsClient(this);
        LC = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                recieveLocation(locationResult);
            }
        };

        LR = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder();
        builder.addLocationRequest(LR);
        LSR=builder.build();
        startLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }



}

