package com.example.jere.theamblepurpose;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//Route class for storing storing and handling static route data from the database.
public class Route {
    private static JSONObject routeData;
    private static int currentPoint;
    private static int lastPointNumber;
    private static ArrayList<JSONObject> routePoints;
    private static Long currentTimer;
    private static double travelledDistance;
    private static double lastLat;
    private static double lastLon;
    private static int routeID;
    private static double requiredDistance;
    private static Long requiredTime;

   public Route(JSONObject routeData) throws JSONException {
       this.routeData = routeData;
       ArrayList<JSONObject> routePoints = new ArrayList<>();
       routePoints.clear();
       this.currentPoint = 0;
       this.currentTimer = 0L;
       this.travelledDistance = 0.0;
       lastPointNumber = routeData.getJSONArray("points").length();
       routeID = routeData.getInt("id");
       double distanceInKilometers = routeData.getDouble("duration_distance");
       requiredDistance = distanceInKilometers * 1000;
       Long requiredTimeInMins = routeData.getLong("duration_time");
       requiredTime = TimeUnit.MINUTES.toMillis(requiredTimeInMins);

       for (int i=0; i < routeData.getJSONArray("points").length(); i++)
       {
           routePoints.add(routeData.getJSONArray("points").getJSONObject(i));
           Log.d("test", routePoints.get(i).toString());
       }
       this.routePoints = routePoints;

   }

   public static JSONObject getJSON(){
      return routeData;
   }



   public static Double getCurrentLatitude() throws JSONException {

       return Double.parseDouble(routePoints.get(currentPoint).get("latitude").toString());
   }

    public static Double getCurrentLongitude() throws JSONException {
        return Double.parseDouble(routePoints.get(currentPoint).get("longitude").toString());
    }

    public static String getCurrentHint() throws JSONException {
       return routePoints.get(currentPoint).get("hint").toString();
    }

    public static void incrementCurrentPoint() {
       currentPoint++;
    }

    public static int getCurrentPoint() {
        return currentPoint;
    }

    public static boolean checkForLastPoint() {

       if (currentPoint == routePoints.size()-1) {
           return true;
       }
       else {
           return false;
       }
    }
    public static Long getCurrentTimer() {
        return currentTimer;
    }

    public static void setCurrentTimer(Long currentTimerFromActivity) {
        currentTimer = currentTimerFromActivity;
    }

    public static void addToDistanceTravelled(double addedDistance) {
        travelledDistance= travelledDistance + addedDistance;
    }
    public static double getTravelledDistance() {
        return travelledDistance;
    }

    public static double getLastLat() {
        return lastLat;
    }

    public static void setLastLat(double lastLat) {
        Route.lastLat = lastLat;
    }

    public static double getLastLon() {
        return lastLon;
    }

    public static void setLastLon(double lastLon) {
        Route.lastLon = lastLon;
    }

    public static int getLastPointNumber() {
        return lastPointNumber;
    }
    public static int getRouteID() {
        return routeID;
    }

    public static Long getRequiredTime() {
        return requiredTime;
    }
    public static double getRequiredDistance() {
        return requiredDistance;
    }
}
