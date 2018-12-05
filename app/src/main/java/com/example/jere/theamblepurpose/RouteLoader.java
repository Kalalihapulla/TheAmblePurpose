package com.example.jere.theamblepurpose;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RouteLoader extends AppCompatActivity {

    private JSONObject routeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.routeloader_activity);

        loadRoutes();

//        try {
//            JSONArray routeArrayList = this.routeArray.getJSONArray("");
//            Log.d("test", routeArrayList.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        final ListView listview = (ListView) findViewById(R.id.routeList);

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        final RouteArrayAdapter adapter = new RouteArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                                adapter.notifyDataSetChanged();

            }

        });



      Button startSelectedRouteButton = (Button)findViewById(R.id.startTestRoute);

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

    public void loadRoutes() {

        Log.d("test", "STARTED ROUTING");

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://206.189.106.84:2121/routes";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        routeArray = response;

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("test", "NO RESPONSE!");
                    }
                });
        queue.add(jsonObjectRequest);

    }



}