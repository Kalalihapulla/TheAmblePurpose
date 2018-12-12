package com.example.jere.theamblepurpose;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class StartScreenActivity extends AppCompatActivity {

    final Context context = this;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startscreen_activity);


        Button selectRouteButton = (Button)findViewById(R.id.startRouteButton);
        Button createRoute = (Button)findViewById(R.id.createRouteButton);
        Button instructionsButton = (Button)findViewById(R.id.instructionsButton);
        final ImageButton accountButton = (ImageButton)findViewById(R.id.accountButton);

        builder = new AlertDialog.Builder(this);

        selectRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartScreenActivity.this, RouteSettingsActivity.class));
            }
        });
        createRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Notice!")
                        .setMessage("This feature is coming soon! Stay tuned!")
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }
                        });
                builder.show();

            }
        });
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Application instructions:")
                        .setMessage("Disclaimer!  " +
                                "There might be changes in the street view pictures based on the time of day and year they were taken!")
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            }
                        });
                builder.show();

            }
        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.accountpopup);

                TextView userName = (TextView) dialog.findViewById(R.id.userName);
                TextView userPoints = (TextView) dialog.findViewById(R.id.userPoints);
                TextView userDistance = (TextView) dialog.findViewById(R.id.userDistance);
                TextView userCompleted = (TextView) dialog.findViewById(R.id.userRoutes);

                userName.setText(TestUser.getName());
                userPoints.setText(String.valueOf("Points: " + TestUser.getTotalPoints()));
                userDistance.setText(String.valueOf("Distance: " + String.format("%.2f", TestUser.getDistanceTravelled())));
                userCompleted.setText(String.valueOf("Routes complete: " + TestUser.getCompleteRoutesAmount()));

                ImageButton cancelButton = (ImageButton) dialog.findViewById(R.id.accountCancelButton);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });


                dialog.show();

            }
        });
    }
}