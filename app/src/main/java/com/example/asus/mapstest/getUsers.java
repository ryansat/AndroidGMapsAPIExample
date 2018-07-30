package com.example.asus.mapstest;

import android.app.LauncherActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.asus.mapstest.NotificationHelper.PRIMARY_CHANNEL;


public class getUsers extends AppCompatActivity  implements MapsFragment.OnFragmentInteractionListener {

    public EditText ed;
    Button button;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_users);
        ed = (EditText) findViewById(R.id.userids);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Bundle bundle = new Bundle();
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(bundle);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.Relatives, mapsFragment).commit();
    }

    public void button1 (View v){
        Bundle bundle = new Bundle();
        bundle.putString("userid", ed.getText().toString());
        MapsFragment mapsFragment = new MapsFragment();
        //String a = ed.getText().toString();
        mapsFragment.setArguments(bundle);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.Relatives, mapsFragment).commit();
        //startTimer();

    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        try {
            updateLocation();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 1000, 5000); //
    }


    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp

                        Bundle bundle = new Bundle();
                        bundle.putString("userid", ed.getText().toString());
                        MapsFragment mapsFragment = new MapsFragment();
                        //String a = ed.getText().toString();
                        mapsFragment.setArguments(bundle);
                        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.Relatives, mapsFragment).commit();
                    }
                });
            }
        };
    }




    public void updateLocation(){
        Bundle bundle = new Bundle();
        bundle.putString("userid", ed.getText().toString());
        MapsFragment mapsFragment = new MapsFragment();
        //String a = ed.getText().toString();
        mapsFragment.setArguments(bundle);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.Relatives, mapsFragment).commit();
    }
}
