package com.example.asus.mapstest;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.List;

public class GetBoundMaps extends FragmentActivity  implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{

    private GoogleMap mMap;
    private LatLngBounds bounds;
    LatLng citys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_bound_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLoadedCallback(this);

        citys = new LatLng(-7.3311384,112.722742);

        List<LatLng> pts = new ArrayList<>();
        pts.add(new LatLng(-7.331122, 112.723060));
        pts.add(new LatLng(-7.3315231,112.7227359));
        pts.add(new LatLng(-7.332139, 112.724556));
        pts.add(new LatLng(-7.331027, 112.723987));


        bounds = new LatLngBounds(pts.get(2), pts.get(1));

        boolean contains1 = PolyUtil.containsLocation(citys.latitude, citys.longitude, pts, true);
        System.out.println("contains1: " + contains1);

        boolean contains2 = bounds.contains(citys);
        System.out.println("contains2: " + contains2);

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(citys).title("Marker in city"));

        for (int i=0; i<pts.size(); i++) {
            int resId = 0;
            /*switch(i) {
                case 0:
                    resId = R.drawable.m1;
                    break;
                case 1:
                    resId = R.drawable.m2;
                    break;
                case 2:
                    resId = R.drawable.m3;
                    break;
                case 3:
                    resId = R.drawable.m4;
                    break;
            }*/
            if (resId != 0) {
                mMap.addMarker(new MarkerOptions().position(pts.get(i)).icon(BitmapDescriptorFactory.fromResource(resId)).title(""+(i+1)));
            }
        }

        mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.BLUE));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(citys));
    }

    @Override
    public void onMapLoaded() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,170));

        this.fixedCode();
        //Toast.makeText(getApplicationContext(),"Maps",Toast.LENGTH_LONG);
    }

    private void fixedCode() {
        //LatLng city = new LatLng(53.121896, 20.210110);

        List<LatLng> pts = new ArrayList<>();
        pts.add(new LatLng(-7.331122, 112.723060));
        pts.add(new LatLng(-7.3315231,112.7227359));
        pts.add(new LatLng(-7.332139, 112.724556));
        pts.add(new LatLng(-7.331027, 112.723987));

        bounds = new LatLngBounds(pts.get(2), pts.get(1));

        boolean contains1 = PolyUtil.containsLocation(citys.latitude, citys.longitude, pts, true);
        System.out.println("contains1: " + contains1);

        boolean contains2 = bounds.contains(citys);
        System.out.println("contains2: " + contains2);

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(citys).title("Marker in city"));

        for (int i=0; i<pts.size(); i++) {
            int resId = 0;
            /*switch(i) {
                case 0:
                    resId = R.drawable.m1;
                    break;
                case 1:
                    resId = R.drawable.m2;
                    break;
                case 2:
                    resId = R.drawable.m3;
                    break;
                case 3:
                    resId = R.drawable.m4;
                    break;
            }*/
            if (resId != 0) {
                mMap.addMarker(new MarkerOptions().position(pts.get(i)).icon(BitmapDescriptorFactory.fromResource(resId)).title(""+(i+1)));
            }
        }

        mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.BLUE));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(citys));

    }
}
