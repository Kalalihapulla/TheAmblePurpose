package com.example.jere.theamblepurpose;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

public class RouteSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routesettings_activity);

        final TimePicker timepicker=(TimePicker)findViewById(R.id.timePicker);

        timepicker.setIs24HourView(true);
        ImageButton acceptSettings = (ImageButton) findViewById(R.id.okButton);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);

        acceptSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RouteSettingsActivity.this, RouteLoader.class));
                Log.d("test", "Current Time: "+timepicker.getHour()+":"+timepicker.getMinute());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RouteSettingsActivity.this, StartScreenActivity.class));
            }
        });

        SeekBar distanceBar = (SeekBar)findViewById(R.id.distanceBar);

        distanceBar.setMax(300);
        distanceBar.setProgress(1);

        final TextView distanceText = (TextView)findViewById(R.id.distanceText);


        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double progressDecimal = ((double) progress / 10.0);
                String progressString = String.valueOf(progressDecimal);
                distanceText.setText(progressString + " KM");

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

    }


}
