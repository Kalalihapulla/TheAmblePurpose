package com.example.jere.theamblepurpose;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RouteSettingsActivity extends AppCompatActivity {
    private double usableDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routesettings_activity);

        final TimePicker timepicker=(TimePicker)findViewById(R.id.timePicker);

        usableDistance = 0;

        timepicker.setIs24HourView(true);
        ImageButton acceptSettings = (ImageButton) findViewById(R.id.okButton);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);

        acceptSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                double hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                double minutes = Calendar.getInstance().get(Calendar.MINUTE);
                double totalMinutes = hours * 60 + minutes;
                double selectedTime = timepicker.getHour() * 60 + timepicker.getMinute();
                double usableTime = selectedTime - totalMinutes;
                if (usableTime < 0) {
                    usableTime = 1440 + usableTime;
                }

//                Log.d("test",String.valueOf(usableTime));
 //               Log.d("test",String.valueOf(usableDistance));

                Intent intent = new Intent(RouteSettingsActivity.this, RouteLoader.class);
                intent.putExtra("timeSetting", usableTime);
                Log.d("test", "usable distance: " + usableDistance);
                intent.putExtra("distanceSetting", usableDistance);
                startActivity(intent);

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
                usableDistance = ((double) progress / 10.0);
                String progressString = String.valueOf(usableDistance);
                distanceText.setText(progressString + " KM");

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

    }


}
