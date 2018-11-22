package com.example.jere.theamblepurpose;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class RouteLoader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.routeloader_activity);

        Button startSelectedRouteButton = (Button)findViewById(R.id.startSelectedRoute);

        startSelectedRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRouteInfo();
            }
        });

    }

    public void loadRouteInfo() {

        Log.d("test", "STARTED ROUTING");

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://206.189.106.84:2121/route?id=7";
        //route?id=1

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.d("test", "GOT RESPONSE");

                         //   Log.d("test", response.getJSONObject("route").getJSONArray("points").getJSONObject(0).get("longitude").toString());
                         //   Log.d("test", response.getJSONObject("route").getJSONArray("points").getJSONObject(0).get("latitude").toString());

                            String longitude = response.getJSONArray("points").getJSONObject(0).get("longitude").toString();
                            String latitude = response.getJSONArray("points").getJSONObject(0).get("longitude").toString();

                            Route routeWithResponseData = new Route(response);
                            startActivity(new Intent(RouteLoader.this, RouteActivity.class));

                            if (Route.getJSON() != null) {
                                startActivity(new Intent(RouteLoader.this, RouteActivity.class));
                            }

                            else {
                                Toast.makeText(getApplicationContext(), "No route data found.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("test", "JSON ERROR!");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("test", "NO RESPONSE!");
                    }
                });

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

}