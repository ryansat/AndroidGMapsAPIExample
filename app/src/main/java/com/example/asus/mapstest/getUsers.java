package com.example.asus.mapstest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class getUsers extends AppCompatActivity  implements MapsFragment.OnFragmentInteractionListener {
public EditText ed;
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

    }
}
