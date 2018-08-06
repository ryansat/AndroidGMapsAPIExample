package com.example.asus.mapstest;

import android.app.LauncherActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.asus.mapstest.NotificationHelper.PRIMARY_CHANNEL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapLoadedCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Marker locations;
    private NotificationHelper noti;
    private GoogleMap mMap;
    public String notif = "Notifikasi";
    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;
    private LatLngBounds bounds;
    LatLng citys;
    private String xResult = "";
    private String xResultBoundary = "";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    public int total =  3;
    public double[] longitude =  new double[total];
    public double[] latitude  =  new double[total];
    public String[] user  =  new String[total];
    public String[] jamaah  =  new String[total];
    public String[] jeniskelamin  =  new String[total];
    //final Handler handler = new Handler();
    LocationCallback mLocationCallback;
    private Timer myTimer;
    //Seusuaikan url dengan nama domain yang anda gunakan
    //private String url = "http://satriaworld.000webhostapp.com/android/daftarmakanan.php";
    private String url = "http://satriaworlds.net/maps/listdata.php";
    private String urlBoundary = "http://satriaworlds.net/maps/listboundary.php";
    private JSONObject jObject;
    public List<LatLng> pts;
    Button button;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    final Runnable r = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1000);
            button.performClick();
        }
    };
    private NotificationManager manager;
    private transient LocationManager locationManager;
    private transient LocationListener locationListener;
    MapView mMapView;
    private GoogleMap googleMap;
    getUsers get;
    public String id;

    //for boundary
    public int boundary = 4;
    public int[] idboundary = new int[boundary];
    public double[] longitudeboundary =  new double[boundary];
    public double[] latitudeboundary  =  new double[boundary];

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main_login);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        id = getArguments().getString("userid");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }



        mMapView.getMapAsync(new OnMapReadyCallback() {
            private void parse() throws Exception {
                jObject = new JSONObject(xResult);
                JSONArray menuitemArray = jObject.getJSONArray("lokasi");
                String sret="";
                for (int i = 0; i < menuitemArray.length(); i++) {

                    sret +=menuitemArray.getJSONObject(i).getString("longitude").toString()+" : ";
                    System.out.println(menuitemArray.getJSONObject(i).getString("longitude").toString());
                    System.out.println(menuitemArray.getJSONObject(i).getString("latitude").toString());
                    System.out.println(menuitemArray.getJSONObject(i).getString("userid").toString());
                    System.out.println(menuitemArray.getJSONObject(i).getString("jamaah").toString());
                    sret +=menuitemArray.getJSONObject(i).getString("latitude").toString()+"\n";
                    sret +=menuitemArray.getJSONObject(i).getString("userid").toString()+"\n";
                    sret +=menuitemArray.getJSONObject(i).getString("jamaah").toString()+"\n";

                    longitude[i] = Double.parseDouble(menuitemArray.getJSONObject(i).getString("longitude").toString());
                    latitude[i] = Double.parseDouble(menuitemArray.getJSONObject(i).getString("latitude").toString());
                    user[i] = String.valueOf(menuitemArray.getJSONObject(i).getString("userid").toString());
                    jamaah[i] = String.valueOf(menuitemArray.getJSONObject(i).getString("jamaah").toString());
                    jeniskelamin[i] = String.valueOf(menuitemArray.getJSONObject(i).getString("jeniskelamin").toString());
                    // longitude = (menuitemArray.getJSONObject(i).getString("longitude").toString());
                    //Toast.makeText(getApplicationContext(), menuitemArray.getJSONObject(i).getString("longitude").toString()+"----"+menuitemArray.getJSONObject(i).getString("latitude").toString(),Toast.LENGTH_LONG).show();
                }
                //txtResult.setText(sret);
            }

            private void parseBoundary() throws Exception {
                jObject = new JSONObject(xResultBoundary);
                JSONArray menuitemArray = jObject.getJSONArray("boundary");
                String sret="";
                for (int i = 0; i < menuitemArray.length(); i++) {

                    sret +=menuitemArray.getJSONObject(i).getString("id").toString()+" : ";
                    System.out.println(menuitemArray.getJSONObject(i).getString("id").toString());
                    System.out.println(menuitemArray.getJSONObject(i).getString("latitude").toString());
                    System.out.println(menuitemArray.getJSONObject(i).getString("longitude").toString());
                    sret +=menuitemArray.getJSONObject(i).getString("latitude").toString()+"\n";
                    sret +=menuitemArray.getJSONObject(i).getString("longitude").toString()+"\n";


                    longitudeboundary[i] = Double.parseDouble(menuitemArray.getJSONObject(i).getString("longitude").toString());
                    latitudeboundary[i] = Double.parseDouble(menuitemArray.getJSONObject(i).getString("latitude").toString());
                    idboundary[i] = Integer.parseInt(menuitemArray.getJSONObject(i).getString("id").toString());

                    // longitude = (menuitemArray.getJSONObject(i).getString("longitude").toString());
                    //Toast.makeText(getApplicationContext(), menuitemArray.getJSONObject(i).getString("longitude").toString()+"----"+menuitemArray.getJSONObject(i).getString("latitude").toString(),Toast.LENGTH_LONG).show();
                }
                //txtResult.setText(sret);
            }

            public String getRequest(String Url){
                String sret="";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(Url);
                try{
                    HttpResponse response = client.execute(request);
                    sret =request(response);
                }catch(Exception ex){
                    //Toast.makeText(this,"Gagal ", Toast.LENGTH_SHORT).show();
                }
                return sret;
            }

            public String getRequestBoundary(String Url){
                String sret="";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(Url);
                try{
                    HttpResponse response = client.execute(request);
                    sret =request(response);
                }catch(Exception ex){
                    Toast.makeText(getContext(),"Gagal "+ex, Toast.LENGTH_SHORT).show();
                }
                return sret;
            }
            @Override
            public void onMapReady(GoogleMap googleMap) {
                xResult = getRequest(url);
                xResultBoundary = getRequestBoundary(url);
                try {
                    parse();
                    parseBoundary();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMap = googleMap;
                myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //updateLocation();
                        //  button.performClick();
                    }

                }, 0, 1000);
                mMap.clear();
                List<LatLng> pts = new ArrayList<>();

                pts.add(new LatLng(21.426717,39.8170513 ));
                pts.add(new LatLng(21.426954, 39.830096));
                pts.add(new LatLng(21.421716, 39.832523));
                pts.add(new LatLng(21.422560, 39.821529));

                bounds = new LatLngBounds(pts.get(2), pts.get(1));
                LatLng mecca = new LatLng(latitude[0],longitude[0]);
                

                MarkerOptions markerOptions = new MarkerOptions() ;
                ArrayList<LatLng> latlngs = new ArrayList<>();
                for (int i = 0; i < total; i ++) {
                    latlngs.add(new LatLng(latitude[i],longitude[i]));


                }

                int ids = Integer.parseInt(id);
                ids = ids-1;
                for (int i = 0; i < total; i ++) {
                    LatLng point = new LatLng(latitude[i],longitude[i]);
                    markerOptions.position(point);
                    markerOptions.title(user[i]);
                    markerOptions.snippet(jamaah[i]);
                    if (jeniskelamin[i].equalsIgnoreCase("Pria")){
                        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                        //mMap.addMarker(markerOptions);
                    }
                    else
                    {
                        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.woman)));
                    }
                }
                try{
                for (int i = 0; i < total; i ++) {
                    boolean contains1 = PolyUtil.containsLocation(latitude[i], longitude[i], pts, true);
                    latlngs.add(new LatLng(latitude[i],longitude[i]));
                    mecca  = new LatLng(latitude[i],longitude[i]);
                    //Toast.makeText(getApplicationContext(),"Lokasi User: "+ jamaah[i]+ ", " + contains1,Toast.LENGTH_SHORT).show();
                    String idChannel = "my_channel_01";
                    Intent mainIntent;

                    mainIntent = new Intent(getContext(), LauncherActivity.class);

                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, mainIntent, 0);

                    NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationChannel mChannel = null;
                    // The id of the channel.

                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), PRIMARY_CHANNEL);

                    if (contains1 == false) {
                        builder.setContentTitle(getContext().getString(R.string.app_name))
                                .setSmallIcon(R.drawable.person)
                                .setContentIntent(pendingIntent)
                                .setContentText("Jamaah "+ jamaah[i]+" Telah Keluar Area");
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mChannel = new NotificationChannel(PRIMARY_CHANNEL, getContext().getString(R.string.app_name), importance);
                        // Configure the notification channel.
                        mChannel.setDescription(("Notif"));
                        mChannel.enableLights(true);
                        mChannel.setLightColor(Color.WHITE);
                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        mNotificationManager.createNotificationChannel(mChannel);
                    } else {
                        builder.setContentTitle(getContext().getString(R.string.app_name))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                //.setColor(getApplicationContext().getColor(getApplicationContext(), R.color.transparent))
                                .setVibrate(new long[]{100, 250})
                                .setLights(Color.YELLOW, 500, 5000)
                                .setAutoCancel(true);
                    }
                    mNotificationManager.notify(i, builder.build());



                }}
                catch (Exception e){

                }

                mecca  = new LatLng(latitude[ids],longitude[ids]);
                mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.RED));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mecca, 17);
                mMap.animateCamera(cameraUpdate);
                // mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.RED));
            }

            public void initializeTimerTask() {

                timerTask = new TimerTask() {
                    public void run() {

                        //use a handler to run a toast that shows the current timestamp
                        handler.post(new Runnable() {
                            public void run() {
                                //get the current timeStamp

                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                                final String strDate = simpleDateFormat.format(calendar.getTime());
                                xResult = getRequest(url);
                                try {

                                    parse();

                                    Marker location;
                                    mMap.clear();
                                    List<LatLng> pts = new ArrayList<>();
                                    pts.add(new LatLng(21.426117,39.8170513 ));
                                    pts.add(new LatLng(21.426954, 39.830096));
                                    pts.add(new LatLng(21.421716, 39.832523));
                                    pts.add(new LatLng(21.422560, 39.821529));


                                    bounds = new LatLngBounds(pts.get(2), pts.get(1));
                                    LatLng mecca = new LatLng(latitude[0],longitude[0]);


                                    //bounds = new LatLngBounds(pts.get(2), pts.get(1));
                                    //LatLng mecca = new LatLng(latitude[0],longitude[0]);


                                    // sydney[i] = new LatLng(latitude[0], longitude[0]);
                            /*for (int i = 0 ; i < total; i++) {
                                boolean contains1 = PolyUtil.containsLocation(latitude[0], longitude[0], pts, true);
                                System.out.println("contains1: " + contains1);
                            }*/

                                    MarkerOptions markerOptions = new MarkerOptions() ;
                                    ArrayList<LatLng> latlngs = new ArrayList<>();
                                    mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.GREEN));

                                    for (int i = 0; i < total; i ++) {
                                        LatLng point = new LatLng(latitude[i],longitude[i]);
                                        markerOptions.position(point);
                                        markerOptions.title(user[i]);
                                        markerOptions.snippet(jamaah[i]);
                                        if (jeniskelamin[i].equalsIgnoreCase("Pria")){
                                            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                                            //mMap.addMarker(markerOptions);
                                        }
                                        else
                                        {
                                            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.woman)));
                                        }
                                    }

                                    for (int i = 0; i < total; i ++) {
                                        boolean contains1 = PolyUtil.containsLocation(latitude[i], longitude[i], pts, true);
                                        latlngs.add(new LatLng(latitude[i],longitude[i]));
                                        mecca  = new LatLng(latitude[i],longitude[i]);
                                        //Toast.makeText(getApplicationContext(),"Lokasi User: "+ jamaah[i]+ ", " + contains1,Toast.LENGTH_SHORT).show();
                                        String idChannel = "my_channel_01";
                                        Intent mainIntent;

                                        mainIntent = new Intent(getContext(), LauncherActivity.class);

                                        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, mainIntent, 0);

                                        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                                        NotificationChannel mChannel = null;
                                        // The id of the channel.

                                        int importance = NotificationManager.IMPORTANCE_HIGH;

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), PRIMARY_CHANNEL);

                                        if (contains1 == false) {
                                            builder.setContentTitle(getContext().getString(R.string.app_name))
                                                    .setSmallIcon(R.drawable.person)
                                                    .setContentIntent(pendingIntent)
                                                    .setContentText("Jamaah "+ jamaah[i]+" Telah Keluar Area");
                                        }

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            mChannel = new NotificationChannel(PRIMARY_CHANNEL, getContext().getString(R.string.app_name), importance);
                                            // Configure the notification channel.
                                            mChannel.setDescription(("Notif"));
                                            mChannel.enableLights(true);
                                            mChannel.setLightColor(Color.WHITE);
                                            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                            mNotificationManager.createNotificationChannel(mChannel);
                                        } else {
                                            builder.setContentTitle(getContext().getString(R.string.app_name))
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    //.setColor(getApplicationContext().getColor(getApplicationContext(), R.color.transparent))
                                                    .setVibrate(new long[]{100, 250})
                                                    .setLights(Color.YELLOW, 500, 5000)
                                                    .setAutoCancel(true);
                                        }
                                        mNotificationManager.notify(i, builder.build());



                                    }
                                    mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.GREEN));
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(21.426717,39.8170513 ), 17);
                                    mMap.animateCamera(cameraUpdate);





                                    //mMap.clear();
                                    //move map camera

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //show the toast
                                int duration = Toast.LENGTH_SHORT;

                                // Toast toast = Toast.makeText(getApplicationContext(), longitude+"--"+latitude, duration);
                                //toast.show();
                            }
                        });
                    }
                };
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public static String request(HttpResponse response){
        String result = "";
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }catch(Exception ex){
            result = "Error";
        }
        return result;
    }






    public void updateLocation(){
        xResult = getRequest(url);
        try {
            parse();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Marker location;
        mMap.clear();
        List<LatLng> pts = new ArrayList<>();

        pts.add(new LatLng(21.426117,39.8170513 ));
        pts.add(new LatLng(21.426954, 39.830096));
        pts.add(new LatLng(21.421716, 39.832523));
        pts.add(new LatLng(21.422560, 39.821529));


        bounds = new LatLngBounds(pts.get(2), pts.get(1));
        LatLng mecca = new LatLng(latitude[0],longitude[0]);



        MarkerOptions markerOptions = new MarkerOptions() ;
        ArrayList<LatLng> latlngs = new ArrayList<>();
        mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.GREEN));

        for (int i = 0; i < total; i ++) {
            LatLng point = new LatLng(latitude[i],longitude[i]);
            markerOptions.position(point);
            markerOptions.title(user[i]);
            markerOptions.snippet(jamaah[i]);
            if (jeniskelamin[i].equalsIgnoreCase("Pria")){
                mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                //mMap.addMarker(markerOptions);
            }
            else
            {
                mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.woman)));
            }
        }

        for (int i = 0; i < total; i ++) {
            boolean contains1 = PolyUtil.containsLocation(latitude[i], longitude[i], pts, true);
            latlngs.add(new LatLng(latitude[i],longitude[i]));
            mecca  = new LatLng(latitude[i],longitude[i]);
            //Toast.makeText(getApplicationContext(),"Lokasi User: "+ jamaah[i]+ ", " + contains1,Toast.LENGTH_SHORT).show();
            String idChannel = "my_channel_01";
            Intent mainIntent;

            mainIntent = new Intent(getContext(), LauncherActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, mainIntent, 0);

            NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = null;
            // The id of the channel.

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), PRIMARY_CHANNEL);

            if (contains1 == false) {
                builder.setContentTitle(getContext().getString(R.string.app_name))
                        .setSmallIcon(R.drawable.person)
                        .setContentIntent(pendingIntent)
                        .setContentText("Jamaah "+ jamaah[i]+" Telah Keluar Area");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(PRIMARY_CHANNEL, getContext().getString(R.string.app_name), importance);
                // Configure the notification channel.
                mChannel.setDescription(("Notif"));
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.WHITE);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);
            } else {
                builder.setContentTitle(getContext().getString(R.string.app_name))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        //.setColor(getApplicationContext().getColor(getApplicationContext(), R.color.transparent))
                        .setVibrate(new long[]{100, 250})
                        .setLights(Color.YELLOW, 500, 5000)
                        .setAutoCancel(true);
            }
            mNotificationManager.notify(i, builder.build());



        }
        mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.GREEN));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(21.426717,39.8170513 ), 17);
        mMap.animateCamera(cameraUpdate);







    }

    private void parse() throws Exception {
        jObject = new JSONObject(xResult);
        JSONArray menuitemArray = jObject.getJSONArray("lokasi");
        String sret="";
        for (int i = 0; i < menuitemArray.length(); i++) {

            sret +=menuitemArray.getJSONObject(i).getString("longitude").toString()+" : ";
            System.out.println(menuitemArray.getJSONObject(i).getString("longitude").toString());
            System.out.println(menuitemArray.getJSONObject(i).getString("latitude").toString());
            System.out.println(menuitemArray.getJSONObject(i).getString("userid").toString());
            System.out.println(menuitemArray.getJSONObject(i).getString("jamaah").toString());
            sret +=menuitemArray.getJSONObject(i).getString("latitude").toString()+"\n";
            sret +=menuitemArray.getJSONObject(i).getString("userid").toString()+"\n";
            sret +=menuitemArray.getJSONObject(i).getString("jamaah").toString()+"\n";

            longitude[i] = Double.parseDouble(menuitemArray.getJSONObject(i).getString("longitude").toString());
            latitude[i] = Double.parseDouble(menuitemArray.getJSONObject(i).getString("latitude").toString());
            user[i] = String.valueOf(menuitemArray.getJSONObject(i).getString("userid").toString());
            jamaah[i] = String.valueOf(menuitemArray.getJSONObject(i).getString("jamaah").toString());
            jeniskelamin[i] = String.valueOf(menuitemArray.getJSONObject(i).getString("jeniskelamin").toString());
            // longitude = (menuitemArray.getJSONObject(i).getString("longitude").toString());
            //Toast.makeText(getApplicationContext(), menuitemArray.getJSONObject(i).getString("longitude").toString()+"----"+menuitemArray.getJSONObject(i).getString("latitude").toString(),Toast.LENGTH_LONG).show();
        }
        //txtResult.setText(sret);
    }

    public String getRequest(String Url){
        String sret="";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(Url);
        try{
            HttpResponse response = client.execute(request);
            sret =request(response);
        }catch(Exception ex){
            //Toast.makeText(this,"Gagal ", Toast.LENGTH_SHORT).show();
        }
        return sret;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        xResult = getRequest(url);
        try {
            parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMap = googleMap;
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //updateLocation();
                //  button.performClick();
            }

        }, 0, 1000);
        mMap.clear();
        List<LatLng> pts = new ArrayList<>();
       /* pts.add(new LatLng(-7.331122, 112.723060));
        pts.add(new LatLng(-7.3315231,112.7227359));
        pts.add(new LatLng(-7.332139, 112.724556));
        pts.add(new LatLng(-7.331027, 112.723987));
        */
        pts.add(new LatLng(21.426717,39.8170513 ));
        pts.add(new LatLng(21.426954, 39.830096));
        pts.add(new LatLng(21.421716, 39.832523));
        pts.add(new LatLng(21.422560, 39.821529));

        bounds = new LatLngBounds(pts.get(2), pts.get(1));
        LatLng mecca = new LatLng(latitude[0],longitude[0]);


        // sydney[i] = new LatLng(latitude[0], longitude[0]);
        boolean contains1 = PolyUtil.containsLocation(latitude[0], longitude[0], pts, true);
        System.out.println("contains1: " + contains1);


        MarkerOptions markerOptions = new MarkerOptions() ;
        ArrayList<LatLng> latlngs = new ArrayList<>();
        for (int i = 0; i < total; i ++) {
            latlngs.add(new LatLng(latitude[i],longitude[i]));
            mecca  = new LatLng(latitude[i],longitude[i]);

        }

        for (int i = 0; i < total; i ++) {
            LatLng point = new LatLng(latitude[i],longitude[i]);
            markerOptions.position(point);
            markerOptions.title(user[i]);
            markerOptions.snippet(jamaah[i]);
            if (jamaah[i].equalsIgnoreCase("Pria")){
                mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                //mMap.addMarker(markerOptions);
            }
            else
            {
                mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.woman)));
            }
        }
        mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.BLACK));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mecca, 17);
        mMap.animateCamera(cameraUpdate);
        // mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.RED));
    }


    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //startTimer();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLoaded() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
