package com.example.jere.theamblepurpose;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.StreetViewSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity
        implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama mStreetViewPanorama;
    private boolean secondLocation = false;
    private ArrayList<Double> coordinates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.routelayout_activity);

        Route route = new Route("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
       Log.d("test",route.getString());
        Route route2 = new Route("BBBBBBBBBBBBBBBBBBBBBBBB");


        SupportStreetViewPanoramaFragment streetViewFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.googleMapStreetViewFragment);
        streetViewFragment.getStreetViewPanoramaAsync(this);

    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;

        getTestCoordinates();

        if (secondLocation) {
            streetViewPanorama.setPosition(new LatLng(coordinates.get(0), coordinates.get(1)), StreetViewSource.OUTDOOR);
        } else {
          //  streetViewPanorama.setPosition(new LatLng(coordinates.get(0), coordinates.get(1)));
        }

        streetViewPanorama.setStreetNamesEnabled(false);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);
        streetViewPanorama.setUserNavigationEnabled(false);
        streetViewPanorama.animateTo(
                new StreetViewPanoramaCamera.Builder().
                        orientation(new StreetViewPanoramaOrientation(20, 20))
                        .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                        .build(), 2000);

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(panoramaChangeListener);

    }

    private StreetViewPanorama.OnStreetViewPanoramaChangeListener panoramaChangeListener =
            new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
                @Override
                public void onStreetViewPanoramaChange(
                        StreetViewPanoramaLocation streetViewPanoramaLocation) {

                }
            };

    public void getTestCoordinates() {

        Log.d("test", "STARTED ROUTING");

        Log.d("test", Route.getString());

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://206.189.106.84:2121/static_json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.d("test", "GOT RESPONSE");

                            Log.d("test", response.getJSONObject("route").getJSONArray("points").getJSONObject(0).get("longitude").toString());
                            Log.d("test", response.getJSONObject("route").getJSONArray("points").getJSONObject(0).get("latitude").toString());

                            String longitude = response.getJSONObject("route").getJSONArray("points").getJSONObject(0).get("longitude").toString();
                            String latitude = response.getJSONObject("route").getJSONArray("points").getJSONObject(0).get("longitude").toString();
                           // coordinates.add(Double.parseDouble(longitude));
                           // coordinates.add(Double.parseDouble(latitude));

                            coordinates.add(31.2);
                            coordinates.add(32.1);

                            Log.d("test", coordinates.get(0).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("test", "FAILED");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("test", "FAILED");
                    }
                });

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


}