package com.example.jere.theamblepurpose;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.CountDownTimer;
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

import org.json.JSONException;

public class RouteActivity extends AppCompatActivity
        implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama mStreetViewPanorama;
    private boolean secondLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.routelayout_activity);

        SupportStreetViewPanoramaFragment streetViewFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.googleMapStreetViewFragment);
        streetViewFragment.getStreetViewPanoramaAsync(this);

    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;

        if (secondLocation) {
            try {
                streetViewPanorama.setPosition(new LatLng(Route.getCurrentLatitude(), Route.getCurrentLongitude()), StreetViewSource.OUTDOOR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initiateRoutePoint();
        } else {
            try {
                streetViewPanorama.setPosition(new LatLng(Route.getCurrentLatitude(), Route.getCurrentLongitude()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initiateRoutePoint();
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


    public void initiateRoutePoint() {

        final CountDownTimer countDownTimer = new CountDownTimer(15 * 1000, 1000) {

            TextView countDownTimerText = (TextView) findViewById(R.id.countDownTimer);
            public void onTick(long millisUntilFinished) {
                countDownTimerText.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                countDownTimerText.setText("Done !");
                Route.incrementCurrentPoint();
                startActivity(new Intent(RouteActivity.this, RouteStatusActivity.class));
            }
        };

        countDownTimer.start();
    }


}