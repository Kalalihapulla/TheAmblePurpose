package com.example.jere.theamblepurpose;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;

//Activity used to track and display the status of the route.
public class RouteStatusActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 20000; /* 20 sec */

    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;

    private ImageButton readyButton;
    private ProgressBar distanceIndicator;
    private Chronometer routeTimer;
    private boolean isStart;
    private Context context = this;
    private RatingBar routeRating;
    private TextView travelledText;
    private ImageButton menuButton;
    private TextView scoreText;

    private double lastLat;
    private double lastLon;
    private double currentLat;
    private double currentLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.routestatus_layout);

        isStart = false;
        routeTimer = (Chronometer) findViewById(R.id.routeTimer);
        routeTimer.setBase(Route.getCurrentTimer());

        distanceIndicator = (ProgressBar) findViewById(R.id.distanceIndicator);

        distanceIndicator.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryPink), android.graphics.PorterDuff.Mode.MULTIPLY);

        TextView routeNumber = (TextView) findViewById(R.id.routeNumber);
        routeNumber.setText("Point number: " + String.valueOf(Route.getCurrentPoint() + 1) + "/" + String.valueOf(Route.getLastPointNumber()));
        travelledText = (TextView) findViewById(R.id.travelledText);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menuButton);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder menuBuilder = new AlertDialog.Builder(RouteStatusActivity.this);

                menuBuilder.setCancelable(true);

                menuBuilder.setTitle("Return to start screen?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                                startActivity(new Intent(RouteStatusActivity.this, StartScreenActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            }
                        });
                menuBuilder.show();

            }
        });


        routeTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                routeTimer = chronometerChanged;
            }
        });

        startStopChronometer(routeTimer);

        if (requestSinglePermission()) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            checkLocation(); //check whether location service is enable or not in your  phone
        }

        readyButton = (ImageButton) findViewById(R.id.readyButton);

        ImageButton hintButton = (ImageButton) findViewById(R.id.showHintButton);

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Route.setLastLat(currentLat);
                    Route.setLastLon(currentLon);
                    validateDistance(compareLocation());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(RouteStatusActivity.this);

                try {
                    builder1.setMessage(Route.getCurrentHint());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Dismiss",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert1 = builder1.create();
                alert1.show();
            }
        });

    }

    //Starts the location updates when connected to the service.
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationUpdates();

        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("test", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("test", "Connection failed. Error: " + connectionResult.getErrorCode());
    }


    //Updates and stores the needed content based on location updates/changes.
    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

        Log.d("test", String.valueOf(Route.getCurrentPoint()));

        if ((lastLat == 0.0) && (lastLon == 0.0) && Route.getCurrentPoint() == 0) {
            lastLat = currentLat;
            lastLon = currentLon;
        }

        if ((lastLat == 0.0) && (lastLon == 0.0) && Route.getCurrentPoint() != 0) {
            lastLat = Route.getLastLat();
            lastLon = Route.getLastLon();
        }

        double distanceBetweenTwoPoints = compareDistance(currentLat, lastLat, currentLon, lastLon, 0.0, 0.0);
        Log.d("test", "moved: " + String.valueOf(distanceBetweenTwoPoints));
        Route.addToDistanceTravelled(distanceBetweenTwoPoints);
        travelledText.setText("Travelled: " + String.format("%.2f", (Route.getTravelledDistance())) + " Meters");
        Log.d("test", "total: " + String.valueOf(Route.getTravelledDistance()));
        lastLat = currentLat;
        lastLon = currentLon;

    }

    //Starts updating the location data.
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(10000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, (LocationListener) this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    //Pop up dialog for needed permissions.
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    //Checks if the location service is enabled.
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //Requests for the location usage permission.
    private boolean requestSinglePermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //Single Permission is granted
                        isPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            isPermission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return isPermission;

    }

    //Gets the current location of the user based on gps data.
    public LatLng getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Log.d("test", currentLocation.toString());
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            return currentLatLng;
        } else {
            Log.d("info", "Location error");
            return null;
        }
    }

    //Initiates the distance comparison between two points.
    public double compareLocation() throws JSONException {
        LatLng currentLatLng = getCurrentLocation();
        double currentLat = currentLatLng.latitude;
        double currentLon = currentLatLng.longitude;
        double comparedLat = Route.getCurrentLatitude();
        double comparedLon = Route.getCurrentLongitude();
        Log.d("test", String.valueOf(comparedLat));
        Log.d("test", String.valueOf(comparedLon));
        double distanceBetweenPoints = compareDistance(currentLat, comparedLat, currentLon, comparedLon, 0.0, 0.0);
        Log.d("test", String.valueOf(distanceBetweenPoints));
        return distanceBetweenPoints;

    }

    //Disables the use of built in physical or virtual back button.
    @Override
    public void onBackPressed() {
        //Disable back button by overriding the default method.
    }

    //Function to calculate the distance in meters between two gps points
    public double compareDistance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    //Checks and tracks the users distance from the correct location.
    public void validateDistance(double distance) {

        if (distance <= 20) {
            startStopChronometer(routeTimer);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

            if (Route.checkForLastPoint()) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.routecompletionpopup);
                dialog.setCancelable(false);

                TextView routeMessage = (TextView) dialog.findViewById(R.id.descText);
                TextView routeDurTaken = (TextView) dialog.findViewById(R.id.durationText);
                TextView routeLen = (TextView) dialog.findViewById(R.id.distanceText);
                scoreText = (TextView) dialog.findViewById(R.id.scoreText);
                routeRating = (RatingBar) dialog.findViewById(R.id.ratingBar);

                ImageButton acceptButton = (ImageButton) dialog.findViewById(R.id.acceptButton);

                routeMessage.setText("Congratulations! You have completed the route!");
                Long completedTime = Route.getCurrentTimer();
                String completedTimeInString = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(Math.abs(completedTime)),
                        TimeUnit.MILLISECONDS.toMinutes(Math.abs(completedTime)) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Math.abs(completedTime))),
                        TimeUnit.MILLISECONDS.toSeconds(Math.abs(completedTime)) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Math.abs(completedTime))));
                routeDurTaken.setText("Time: " + completedTimeInString);
                String roundedDistance = String.format("%.2f", Route.getTravelledDistance());
                routeLen.setText("Distance travelled: " + roundedDistance + " meters");

                final int totalRoutePoints = calculatePoints(completedTime, Route.getTravelledDistance(), Route.getRequiredTime(), Route.getRequiredDistance());

                scoreText.setText("Your score: " + String.valueOf(totalRoutePoints));

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double numberOfStars = routeRating.getRating();
                        Log.d("test", "star: " + String.valueOf(numberOfStars));
                        putRouteRating(numberOfStars, Route.getRouteID());
                        TestUser.addDistance(Route.getTravelledDistance());
                        TestUser.addPoints(totalRoutePoints);
                        TestUser.addCompletedRoute();
                        startActivity(new Intent(RouteStatusActivity.this, RouteLoader.class));
                    }
                });

                dialog.show();

            } else {
                builder2.setMessage("Congratulations, location reached! Are you ready for the next location?");
                builder2.setCancelable(true);

                builder2.setPositiveButton(
                        "I'm ready!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Route.incrementCurrentPoint();
                                startActivity(new Intent(RouteStatusActivity.this, RouteActivity.class));
                                dialog.cancel();
                            }
                        });
                AlertDialog alert2 = builder2.create();
                alert2.show();
            }


        } else if (distance <= 80) {
            distanceIndicator.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorGreen), android.graphics.PorterDuff.Mode.MULTIPLY);
            Toast toast = Toast.makeText(getApplicationContext(), "You are really close to the destination!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();

        } else if (distance <= 160) {
            distanceIndicator.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorYellow), android.graphics.PorterDuff.Mode.MULTIPLY);
            Toast toast = Toast.makeText(getApplicationContext(), "You are getting closer to the destination!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();
        } else {
            distanceIndicator.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
            Toast toast = Toast.makeText(getApplicationContext(), "You are far away from the destination!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
            toast.show();
        }
    }

    //Handles the route timing functionality with a chronometer.
    public void startStopChronometer(View view) {
        if (isStart) {
            long elapsedMillis = routeTimer.getBase() - SystemClock.elapsedRealtime();
            Route.setCurrentTimer(elapsedMillis);
            Log.d("test", "stopped and set timer to:" + String.valueOf(elapsedMillis));
            routeTimer.stop();
            isStart = false;

        } else {
            routeTimer.setBase(SystemClock.elapsedRealtime() + Route.getCurrentTimer());
            Log.d("test", "current time:" + String.valueOf(Route.getCurrentTimer()));
            Log.d("test", String.valueOf(routeTimer.getBase()));
            routeTimer.start();
            isStart = true;
        }
    }

    //Uses http PUT to upload the route rating given by user to the specific route database.
    public void putRouteRating(final double rating, int routeID) {

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        String url = "http://206.189.106.84:2121/rate?route_id=" + routeID + "&user_id=6&rating=" + rating;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("test", "Star rating of " + rating + " given.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {

        };

        queue.add(putRequest);
    }

    //Calculates and tracks the distance between the users current and previous location.
    public int calculatePoints(Long usedTime, double usedDistance, Long requiredTime, double requiredDistance) {

        Long timeInCorrectForm = Math.abs(usedTime);
        Long timePoints = requiredTime / timeInCorrectForm;
        double distancePoints = requiredDistance / usedDistance;
        int timePointsInInt = toIntExact(timePoints);
        int distancePointsInInt = (int) distancePoints;
        int totalPoints = (timePointsInInt + distancePointsInInt) * 100;
        Log.d("test", "totalpoints " + String.valueOf(totalPoints));
        return totalPoints;
    }
}

