package com.example.osmtest2;

import android.content.Context;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;


import java.util.ArrayList;

public class MapRoute {
    private final MapView map;
    private final Context ctx;
    private static final String USER_AGENT = "JamesOliver";

    public MapRoute(MapView main_map, Context context) {
        map = main_map;
        this.ctx = context;
    }

    public void addRoute(double cd_long, double cd_lat) {

        //Create Road Manager to generate route and set transportation type

        OSRMRoadManager roadManager = new OSRMRoadManager(ctx, USER_AGENT);
        roadManager.setMean(OSRMRoadManager.MEAN_BY_BIKE);

        //Store start and end points for route in array

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        GeoPoint startPoint = new GeoPoint(51.4551, -0.9787); //will be GPS location
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(cd_long, cd_lat);
        waypoints.add(endPoint);

        // generate route and draw lines

        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

        // add route to map

        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }
}
