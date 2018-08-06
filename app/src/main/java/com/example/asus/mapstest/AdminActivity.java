package com.example.asus.mapstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AdminActivity extends AppCompatActivity {
    public String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        userid = getIntent().getStringExtra("USERID");
    }


    public void getLokasi(View v){
        Intent a = new Intent(this,MapsActivity.class);
        userid = getIntent().getStringExtra("USERID");
        a.putExtra("USERID", userid);
        startActivity(a);
    }

    public void cariUser(View v){
        Intent a = new Intent(this,getUsers.class);
        startActivity(a);
    }

    public void notifAlrm(View v){
        Intent a = new Intent(this,getUserLocation.class);
        startActivity(a);
    }
}
