package com.example.jere.theamblepurpose;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RouteLoader extends AppCompatActivity {

    private JSONArray routeArray;
    final Context context = this;
    double usableTime;
    double usableDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.routeloader_activity);

        usableTime = getIntent().getDoubleExtra("timeSetting", 120.0);
        usableDistance = getIntent().getDoubleExtra("distanceSetting", 10.0);

        loadRoutes();

    }

    public void loadRouteInfo(int routeID) {

        Log.d("test", "STARTED ROUTING");

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://206.189.106.84:2121/route?id=" + routeID;
        //route?id=1

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.d("test", "GOT RESPONSE");

                            String longitude = response.getJSONArray("points").getJSONObject(0).get("longitude").toString();
                            String latitude = response.getJSONArray("points").getJSONObject(0).get("longitude").toString();

                            Route routeWithResponseData = new Route(response);
                            startActivity(new Intent(RouteLoader.this, RouteActivity.class));

                            if (Route.getJSON() != null) {
                                startActivity(new Intent(RouteLoader.this, RouteActivity.class));
                            } else {
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

    public void loadRoutes() {


        Log.d("test", "STARTED ROUTING");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http://206.189.106.84:2121/routes?time=" + usableTime +  "?distance=" + usableDistance;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("test", "Routes get");
                        routeArray = response;

                        final ArrayList<JSONObject> routeArrayList = new ArrayList<>();

                        RouteArrayAdapter routeListAdapter = new RouteArrayAdapter(getApplicationContext(), routeArrayList);

                        ListView routeListView = (ListView) findViewById(R.id.routeList);
                        routeListView.setAdapter(routeListAdapter);

                        routeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                final int position = i;

                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.routepopup);
                                try {
                                    dialog.setTitle(routeArrayList.get(i).getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // set the custom dialog components - text, image and button
                                TextView routeDesc = (TextView) dialog.findViewById(R.id.routeDesc);
                                TextView routeDur = (TextView) dialog.findViewById(R.id.routeDuration);
                                TextView routeLen = (TextView) dialog.findViewById(R.id.routeLength);
                                TextView routeRating = (TextView) dialog.findViewById(R.id.routeRating);

                                try {
                                    routeDesc.setText(routeArrayList.get(i).getString("description"));
                                    routeDur.setText("Duration: " + routeArrayList.get(i).getString("duration_time"));
                                    routeLen.setText("Length " + routeArrayList.get(i).getString("duration_distance"));
                                    routeRating.setText("Rating: " + routeArrayList.get(i).getString("avg_rating"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ImageButton acceptButton = (ImageButton) dialog.findViewById(R.id.acceptButton);
                                ImageButton cancelButton = (ImageButton) dialog.findViewById(R.id.cancelButton);

                                acceptButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            loadRouteInfo(routeArrayList.get(position).getInt("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                     dialog.cancel();
                                    }
                                });

                                dialog.show();

                            }
                        });

                        for (int i = 0; i < routeArray.length(); ++i) {
                            try {
                                routeArrayList.add(routeArray.getJSONObject(i));
                                Log.d("test", routeArrayList.get(i).getString("name"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred

                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

    }
}