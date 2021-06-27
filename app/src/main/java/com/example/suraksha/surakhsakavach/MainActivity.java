package com.example.suraksha.surakhsakavach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity  implements SensorEventListener, LocationListener {

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    Sensor accelerometer;

    double accelerationx;
    double accelerationy;
    double accelerationz;
    double speed;
    double longitudedata;
    double lattitudedata;
    int accident;
    Button logout,simulateaccident;
    AlertDialog.Builder builder;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout);
        simulateaccident = findViewById(R.id.button2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = getBaseContext().getFileStreamPath("userphone");
                file.delete();
                Intent intent = new Intent(getApplicationContext(), loginactivity.class);
                startActivity(intent);

            }
        });

        simulateaccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout mainactivity = findViewById(R.id.mainactivitylayout);
                SmsManager sms = SmsManager.getDefault();
                TextView alterttext = findViewById(R.id.textView3);
                ImageView alert = findViewById(R.id.imageView2);
                int c;
                String phoneno="";
                try {
                    FileInputStream fis = openFileInput("userphone");
                    while ((c = fis.read()) != -1) {
                        phoneno = phoneno + (char) c;
                    }
                    fis.close();
                }catch (Exception e){

                }
                mainactivity.setBackgroundColor(Color.RED);
                alert.setVisibility(View.VISIBLE);
                sms.sendTextMessage(phoneno, null, "Accident may have been detected at Longitude: " + longitudedata + " Latitude: " + lattitudedata, null, null);
                alterttext.setText("Accident Detected");
            }
        });

        getSupportActionBar().hide();


        //   builder = new AlertDialog.Builder(this);
        //    builder.setMessage("Please turn on the GPS manually")
        //          .setCancelable(false)
        //        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        //          public void onClick(DialogInterface dialog, int id) {
        //        }
        //       }) .setNegativeButton("No,Exit", new DialogInterface.OnClickListener() {
        //    public void onClick(DialogInterface dialog, int id) {
        //      finish();
        //    dialog.cancel();
        //        }
        //     });
        //     AlertDialog alert = builder.create();
        //    alert.setTitle("Alert");
        //    alert.show();



        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            try {
                sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "onCreate:Registered accelerometer listener");
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                this.onLocationChanged(null);
            }catch(Exception error){

            }

        }else{
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Please turn on the GPS")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }) .setNegativeButton("No,Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.setTitle("GPS turned off");
            alert.show();
            finish();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Please allow the permissions")
                    .setCancelable(false)
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }) .setNegativeButton("No,Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.setTitle("GPS turned off");
            alert.show();
            //    ActivityCompat#requestPermissions
            return;
        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView accdata = findViewById(R.id.accdata);
        Log.d(TAG, "onSensorChanged: X:" + event.values[0]);
        accelerationx = event.values[0]/9.8;
        accelerationy = event.values[1]/9.8;
        accelerationz = event.values[2]/9.8;
        if (accelerationx < 1) {
            accdata.setText("g force: 0");
        } else {
            accdata.setText("g force:" + event.values[0] / 9.8);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        TextView speeddata = findViewById(R.id.speeddata);
        TextView longitude = findViewById(R.id.longitudelabel);
        TextView latitude = findViewById(R.id.latitudelabel);
        ImageView alert = findViewById(R.id.imageView2);

        RelativeLayout mainactivity = findViewById(R.id.mainactivitylayout);

        TextView alterttext = findViewById(R.id.textView3);

        if (location == null) {
            speeddata.setText("waiting for GPS");
        } else {
            try {
                speed = location.getSpeed() / 1000;
                longitudedata = location.getLongitude();
                lattitudedata = location.getLatitude();
                speeddata.setText("Speed(Km/h):" + location.getSpeed()/1000);
                longitude.setText("Longitude:" + longitudedata);
                latitude.setText("Latitude:" + lattitudedata);
                SmsManager sms = SmsManager.getDefault();
                if (speed > 40 & (accelerationx > 90) || accelerationy >90 || accelerationz>90) {
                    int c;
                    String phoneno="";
                    try {
                        FileInputStream fis = openFileInput("userphone");
                        while ((c = fis.read()) != -1) {
                            phoneno = phoneno + (char) c;
                        }
                        fis.close();
                    }catch (Exception e){

                    }
                    mainactivity.setBackgroundColor(Color.RED);
                    alert.setVisibility(View.VISIBLE);
                    sms.sendTextMessage("+91"+phoneno, null, "Accident may have been detected at Longitude: " + longitudedata + " Latitude: " + lattitudedata, null, null);
                    alterttext.setText("Accident Detected");

                }
            }catch (Exception error){

            }

        }
    }
}
