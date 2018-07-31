package com.example.asus.mapstest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_LOCATION_REQUEST_CODE = 99;
    public GoogleMap mMap;
    //public static MapFragment mapFragment;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;
    public Location lokasi;
    int requestCode;
    String[] permissions;
    int[] grantResults;
    private LocationManager locationManager;
    private String provider;
    public float lat;
    public float lng;
    public Locations loc;
    public String userid;
    //private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        final Button button = findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*double longitude = lokasi.getLongitude();
                double latitude = lokasi.getLatitude();
                //lokasi = location;
                //Toast.makeText(getApplicationContext(), "Longitude : " + longitude + "\n Latitude : " + latitude, Toast.LENGTH_LONG).show();
                float lat = (float) (lokasi.getLatitude());
                float lng = (float) (lokasi.getLongitude());
                Log.i("Latitude", "Lattitude:" +lat);
                Log.i("Longitude", "Longitude:" +lng);
                LatLng latLng = new LatLng(lat,lng);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                mMap.animateCamera(cameraUpdate);
                */
                userid = getIntent().getStringExtra("USERID");
                loc = new Locations();
                loc.setId(userid);
                loc.setLatitude(String.valueOf(lat));
                loc.setLongitude(String.valueOf(lng));
                new HttpAsyncTaskPost().execute("http://satriaworlds.net/maps/tambah.php?id="+userid+"&latitude="+lat+"&longitude="+lng);
            }
        });
       // onMyLocationButtonClick();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            System.out.println("Location not avilable");
        }


    }

    public static String POST(String url, Locations loc){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("title", loc.getIDS());
            jsonObject.accumulate("author", loc.getLongitude());
            jsonObject.accumulate("sinopsis", loc.getLatitude());



            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private class HttpAsyncTaskPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0], loc);
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),"User : "+ userid + ", Lat : "+lat+", Long : "+lng,Toast.LENGTH_SHORT).show();
           // Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();

        }
    }
    //@SuppressLint("MissingPermission")
    //@RequiresApi(api = Build.VERSION_CODES.M)


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        Toast.makeText(getApplicationContext(), lat+"----"+lng,Toast.LENGTH_LONG).show();
        Log.i("Latitude", "Lattitude:" +lat);
        Log.i("Longitude", "Longitude:" +lng);
        LatLng latLng = new LatLng(lat,lng);
        LatLng sydney = new LatLng(lat, lng);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in.."));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "This Apps Need Permision", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        lokasi = location;
       // Toast.makeText(this, "Current longitude:\n" + longitude + "\n Current latitude:\n" + latitude, Toast.LENGTH_LONG).show();
        Toast.makeText(this,"User : "+ userid + ", Lat : "+lat+", Long : "+lng,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "Get Location", Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"User : "+ userid + ", Lat : "+lat+", Long : "+lng,Toast.LENGTH_SHORT).show();
        return false;
    }


    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates((LocationListener) this);

    }


    public void onLocationChanged(Location location) {

        lat = (float) (location.getLatitude());
        lng = (float) (location.getLongitude());
        Toast.makeText(getApplicationContext(), lat+"----"+lng,Toast.LENGTH_LONG).show();
        Log.i("Latitude", "Lattitude:" +lat);
        Log.i("Longitude", "Longitude:" +lng);
        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        loc = new Locations();
        userid = getIntent().getStringExtra("USERID");
        loc.setId(userid);
        loc.setLatitude(String.valueOf(lat));
        loc.setLongitude(String.valueOf(lng));
        new HttpAsyncTaskPost().execute("http://satriaworlds.net/maps/tambah.php?id="+userid+"&latitude="+lat+"&longitude="+lng);
        Toast.makeText(this,"User : "+ userid + ", Lat : "+lat+", Long : "+lng,Toast.LENGTH_SHORT).show();


    }



    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        //Location location = mLocationClient.getLastLocation();
        LatLng latLng = new LatLng(lokasi.getLatitude(), lokasi.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}