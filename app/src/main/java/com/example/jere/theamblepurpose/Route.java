package com.example.jere.theamblepurpose;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Route {
    private static JSONObject routeData;
    private static Double currentLatitude;
    private static Double currentLongitude;
    private static int currentPoint;
    private static ArrayList<JSONObject> routePoints;

   public Route(JSONObject routeData) throws JSONException {
       this.routeData = routeData;
       ArrayList<JSONObject> routePoints = new ArrayList<>();
       routePoints.clear();
       this.currentPoint = 0;

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

    public static void incrementCurrentPoint() {
       currentPoint++;
    }

}
