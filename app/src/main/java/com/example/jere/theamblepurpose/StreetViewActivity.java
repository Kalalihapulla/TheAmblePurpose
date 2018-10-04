package com.example.jere.theamblepurpose;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.StreetViewSource;

import java.util.ArrayList;

public class StreetViewActivity extends AppCompatActivity
        implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama mStreetViewPanorama;
    private boolean secondLocation = false;

    public ArrayList generateCoordinates() {
        //Double randomLat = -90 + Math.random() * (90 - (-90));
        //Double randomLon = -180 + Math.random() * (180 - (-180));
        Double randomLat = 60.195805 + Math.random() * (60.3 - 60.195805);
        Double randomLon = 24.938192 + Math.random() * (25 - 24.938192);
        //60.195805, 24.938192
        ArrayList<Double> coordinates = new ArrayList<>();
        coordinates.add(Math.round(randomLat * 1000000.0) / 1000000.0);
        coordinates.add(Math.round(randomLon * 1000000.0) / 1000000.0);
        //coordinates.add(60.221367);
        //coordinates.add(24.8068473);

        Log.d("asd ", Double.toString(randomLat));
        Log.d("asd", Double.toString(randomLon));
        return coordinates;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.streetview_activity);

        SupportStreetViewPanoramaFragment streetViewFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.googleMapStreetView);
        streetViewFragment.getStreetViewPanoramaAsync(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            int counter=0;
            @Override
            public void onClick(View view) {
                secondLocation = !secondLocation;
                onStreetViewPanoramaReady(mStreetViewPanorama);
                counter++;
                Toast.makeText(getApplicationContext(), Integer.toString(counter), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;

        ArrayList<Double> coordinates = generateCoordinates();
        if (secondLocation) {
            streetViewPanorama.setPosition(new LatLng(coordinates.get(0), coordinates.get(1)), StreetViewSource.OUTDOOR);
        } else {
            streetViewPanorama.setPosition(new LatLng(coordinates.get(0), coordinates.get(1)));
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


//                    Toast.makeText(getApplicationContext(), "Lat: " + streetViewPanoramaLocation.position.latitude + " Lng: " + streetViewPanoramaLocation.position.longitude, Toast.LENGTH_SHORT).show();

                }
            };
}