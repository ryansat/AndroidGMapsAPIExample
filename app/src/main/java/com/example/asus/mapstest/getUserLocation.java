package com.example.asus.mapstest;

import android.Manifest;
import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.List;

import static com.example.asus.mapstest.NotificationHelper.PRIMARY_CHANNEL;


public class getUserLocation extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapLoadedCallback {
    private Marker locations;
    private NotificationHelper noti;
    private GoogleMap mMap;
    public Locations loc;
    public String notif = "Notifikasi";
    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;
    private LatLngBounds bounds;
    double latitude1,longitude1;
    LatLng citys;
    private String xResult = "";
    private String xResultBoundary = "";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    public int total =  3; //jumlah user
    public double[] longitude =  new double[total];
    public double[] latitude  =  new double[total];
    public String[] user  =  new String[total];
    public String[] jamaah  =  new String[total];
    public String[] jeniskelamin  =  new String[total];

    //for boundary
    public int boundary = 4;
    public int[] idboundary = new int[boundary];
    public double[] longitudeboundary =  new double[boundary];
    public double[] latitudeboundary  =  new double[boundary];

    LocationCallback mLocationCallback;
    private Timer myTimer;
    //Seusuaikan url dengan nama domain yang anda gunakan
    //private String url = "http://satriaworld.000webhostapp.com/android/daftarmakanan.php";
    private String url = "http://satriaworlds.net/maps/listdata.php";
    private String urlBoundary = "http://satriaworlds.net/maps/listboundary.php";
    private JSONObject jObject;
    public List<LatLng> pts;
    public String output;
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
    public boolean[] isupdated =  new boolean[total];
    public String httpurls;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public String[] callcenter = new String[1];


    //final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_location);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        xResult = getRequest(url);
        try {
            parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //get Json Boundary
        xResultBoundary = getRequestBoundary(urlBoundary);
        try {
            parseBoundary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //TextView txtResult = (TextView)findViewById(R.id.TextViewResult);



    }

    public void getCallcenter(){
                    String login_url = "http://satriaworlds.net/maps/getcallcenter.php";

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String user_name = "1";
                    String pswd = "1";
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
                            + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pswd, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    output = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        output += line;
                    }
                    callcenter[0] = output;
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private String getTitlePrimaryText() {
        // return titlePrimary.getText().toString();
        return notif;

    }

    private String getTitleSecondaryText() {
        //     return titleSecondary.getText().toString();
        return notif;
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

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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
                        xResultBoundary = getRequestBoundary(url);

                        try {

                            parse();
                            getCallcenter();


                            Marker location;
                            mMap.clear();
                            int bound = 0;
                            List<LatLng> pts = new ArrayList<>();
                            for (; bound < boundary; bound++)
                            {
                                pts.add(new LatLng(latitudeboundary[bound],longitudeboundary[bound]));
                                if (bound == 1)
                                {
                                    latitude1 = latitudeboundary[bound];
                                    longitude1 = longitudeboundary[bound];

                                }
                            }

                            /*
                            pts.add(new LatLng(21.426117, 39.8170513));
                            pts.add(new LatLng(21.426954, 39.830096));
                            pts.add(new LatLng(21.421716, 39.832523));
                            pts.add(new LatLng(21.422560, 39.821529));
                            */


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

                                mainIntent = new Intent(getApplicationContext(), LauncherActivity.class);

                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0);

                                NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                                NotificationChannel mChannel = null;
                                // The id of the channel.

                                int importance = NotificationManager.IMPORTANCE_HIGH;

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL);

                                if (contains1 == false)
                                {
                                    builder.setContentTitle(getApplicationContext().getString(R.string.app_name))
                                            .setSmallIcon(R.drawable.person)
                                            .setContentIntent(pendingIntent)
                                            .setContentText("Jamaah "+ jamaah[i]+" Telah Keluar Area");
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        mChannel = new NotificationChannel(PRIMARY_CHANNEL, getApplicationContext().getString(R.string.app_name), importance);
                                        // Configure the notification channel.
                                        mChannel.setDescription(("Notif"));
                                        mChannel.enableLights(true);
                                        mChannel.setLightColor(Color.WHITE);
                                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                        mNotificationManager.createNotificationChannel(mChannel);
                                    } else {
                                        builder.setContentTitle(getApplicationContext().getString(R.string.app_name))
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                //.setColor(getApplicationContext().getColor(getApplicationContext(), R.color.transparent))
                                                .setVibrate(new long[]{100, 250})
                                                .setLights(Color.YELLOW, 500, 5000)
                                                .setAutoCancel(true);
                                    }
                                    if (isupdated[i] == true)

                                    {
                                        int newisupdated = 0;
                                        String urls;
                                        mNotificationManager.notify(i, builder.build());
                                        urls = "http://satriaworlds.net/maps/updatenotif.php?id="+user[i]+"&isupdated="+newisupdated;
                                        loc = new Locations();
                                        loc.setId(user[i]);
                                        loc.setpdated(newisupdated);
                                        new getUserLocation.HttpAsyncTaskPost().execute("http://satriaworlds.net/maps/updatenotif.php?id="+user[i]+"&isupdated="+newisupdated);
                                        //Toast.makeText(this,"User : "+ user[i] + ", Isupdated : "+newisupdated,Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(),"User : "+ user[i] + ", Isupdated : "+newisupdated,Toast.LENGTH_SHORT).show();
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                                            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                                                    == PackageManager.PERMISSION_DENIED) {

                                                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                                                String[] permissions = {Manifest.permission.SEND_SMS};

                                                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

                                            }
                                        }
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(callcenter[0], null, "User "+user[i]+" Telah Keluar Area", null, null);
                                    }
                                }



                            }
                            mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.GREEN));
                            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(21.426717,39.8170513 ), 17);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude1,longitude1 ), 17);

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

        for (int bound = 0; bound < boundary; bound++)
        {
            pts.add(new LatLng(latitudeboundary[bound],longitudeboundary[bound]));
        }
        /*
        pts.add(new LatLng(21.426117,39.8170513 ));
        pts.add(new LatLng(21.426954, 39.830096));
        pts.add(new LatLng(21.421716, 39.832523));
        pts.add(new LatLng(21.422560, 39.821529));
        */

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

            mainIntent = new Intent(getApplicationContext(), LauncherActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0);

            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = null;
            // The id of the channel.

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL);

            if (contains1 == false)
            {
                builder.setContentTitle(getApplicationContext().getString(R.string.app_name))
                        .setSmallIcon(R.drawable.person)
                        .setContentIntent(pendingIntent)
                        .setContentText("Jamaah "+ jamaah[i]+" Telah Keluar Area");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mChannel = new NotificationChannel(PRIMARY_CHANNEL, getApplicationContext().getString(R.string.app_name), importance);
                    // Configure the notification channel.
                    mChannel.setDescription(("Notif"));
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.WHITE);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                } else {
                    builder.setContentTitle(getApplicationContext().getString(R.string.app_name))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            //.setColor(getApplicationContext().getColor(getApplicationContext(), R.color.transparent))
                            .setVibrate(new long[]{100, 250})
                            .setLights(Color.YELLOW, 500, 5000)
                            .setAutoCancel(true);
                }
                if (isupdated[i] == true)

                {
                    int newisupdated = 0;
                    String urls;
                    mNotificationManager.notify(i, builder.build());
                    //SmsManager smsManager = SmsManager.getDefault();
                   // smsManager.sendTextMessage("085648757246", null, "User "+user[i]+" Telah Keluar Area", null, null);
                    urls = "http://satriaworlds.net/maps/updatenotif.php?id="+user[i]+"&isupdated="+newisupdated;
                    loc = new Locations();
                    loc.setId(user[i]);
                    loc.setpdated(newisupdated);
                    new getUserLocation.HttpAsyncTaskPost().execute("http://satriaworlds.net/maps/updatenotif.php?id="+user[i]+"&isupdated="+newisupdated);
                    Toast.makeText(this,"User : "+ user[i] + ", Isupdated : "+newisupdated,Toast.LENGTH_SHORT).show();


                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                        if (checkSelfPermission(Manifest.permission.SEND_SMS)
                                == PackageManager.PERMISSION_DENIED) {

                            Log.d("permission", "permission denied to SEND_SMS - requesting it");
                            String[] permissions = {Manifest.permission.SEND_SMS};

                            requestPermissions(permissions, PERMISSION_REQUEST_CODE);

                        }
                    }
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(callcenter[0], null, "User "+user[i]+" Telah Keluar Area", null, null);
                }
            }





        }
        mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.GREEN));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(21.426717,39.8170513 ), 17);
        mMap.animateCamera(cameraUpdate);







    }


    @Override
    public void onResume() {
        super.onResume();
        startTimer();

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
            if (String.valueOf(menuitemArray.getJSONObject(i).getString("isupdated").toString()).equalsIgnoreCase("1"))
                {
                    isupdated[i] = true;
                }
            else
                {
                    isupdated[i] = false;
                }
           // longitude = (menuitemArray.getJSONObject(i).getString("longitude").toString());
            //Toast.makeText(getApplicationContext(), menuitemArray.getJSONObject(i).getString("longitude").toString()+"----"+menuitemArray.getJSONObject(i).getString("latitude").toString(),Toast.LENGTH_LONG).show();
        }
        //txtResult.setText(sret);
    }

    //parsing boundary
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
    /**
     * Method untuk Mengirimkan data kes erver
     * event by button login diklik
     *

     */
    public String getRequest(String Url){
        String sret="";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(Url);
        try{
            HttpResponse response = client.execute(request);
            sret =request(response);
        }catch(Exception ex){
            Toast.makeText(this,"Gagal "+ex, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this,"Gagal "+ex, Toast.LENGTH_SHORT).show();
        }
        return sret;
    }
    /**
     * Method untuk Menenrima data dari server
     * @param response
     * @return
     */
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


    public void onMapLoaded() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,170));

        this.updateLocation();
        //Toast.makeText(getApplicationContext(),"Maps",Toast.LENGTH_LONG);
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
                    if (jeniskelamin[i].equalsIgnoreCase("Pria")){
                        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
                        //mMap.addMarker(markerOptions);
                    }
                    else
                    {
                        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.woman)));
                    }
                }
            mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.BLUE));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mecca, 17);
            mMap.animateCamera(cameraUpdate);
           // mMap.addPolygon(new PolygonOptions().addAll(pts).strokeColor(Color.RED));
        }



        //Toast.makeText(getApplicationContext(), latitude+"----"+longitude,Toast.LENGTH_SHORT).show();





    @Override
    public void onLocationChanged(Location location) {
        startTimer();


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startTimer();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            jsonObject.accumulate("id", loc.getIDS());
            jsonObject.accumulate("updatestatus", loc.getUpdate());



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
        protected String doInBackground(String... url) {
            return POST(url[0], loc);
        }
        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(getApplicationContext(),"User : "+ userid + ", Lat : "+lat+", Long : "+lng,Toast.LENGTH_SHORT).show();
             Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();

        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }





}
