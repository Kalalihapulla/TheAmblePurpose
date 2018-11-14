package com.example.jere.theamblepurpose;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startscreen_activity);

        Button startActivity = (Button)findViewById(R.id.startactivity_button);
        Button mapTestButton = (Button)findViewById(R.id.maptest_button);
        Button startRouteButton = (Button)findViewById(R.id.startRouteButton);

        startActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartScreenActivity.this, StreetViewActivity.class));
            }
        });

        mapTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartScreenActivity.this, MapActivity.class));
            }
        });

        startRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartScreenActivity.this, RouteActivity.class));
            }
        });
    }
}