package com.example.findyourtoilet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public ArrayList<Toilet> toiletsInAarhus = new ArrayList<Toilet>();
    public ArrayList<MarkerOptions> toiletsMarkers = new ArrayList<MarkerOptions>();
    public int nuOfToilets;
    public GoogleMap mMap;
    boolean mapReady = false;
    private DatabaseReference ref;

    SupportMapFragment mapFragment;

    static final String REQUEST_URL = "https://cc-p-opendatadenmark.ckan.io/dataset/cf1c6b95-3d1f-4cb7-a67b-e93e2de5299c/resource/065550c2-44b0-41db-94ce-009c47b0ba2b/download/bytoiletterwgs84.json";

    static final CameraPosition AARHUS = CameraPosition.builder()
            .target(new LatLng(56.150408, 10.203240))
            .zoom(11)
            .bearing(0)
            .tilt(45)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        GetToiletsAsync task = new GetToiletsAsync();
        task.execute(REQUEST_URL);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

      ref=FirebaseDatabase.getInstance().getReference();


    }


    public void addMaker(View v) {

        for(int i=0;i<nuOfToilets;i++)
        {
            mMap.addMarker(toiletsMarkers.get(i));
            String child="Toilet"+Integer.toString(i);
            ref.child(child).setValue(toiletsInAarhus.get(i));
        }


    }

    public void showRecycleView(View v)
    {
        Intent intent=new Intent(this,ToiletList.class);
        intent.putExtra("toiletsInAarhus",toiletsInAarhus);
        startActivity(intent);
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
        mapReady=true;
        mMap = googleMap;
       // flyTo(myLocation);
       //mMap.addMarker(toilet1);
       flyTo(AARHUS);
    }

    private void flyTo(CameraPosition target)
    {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream is = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                is = urlConnection.getInputStream();
                jsonResponse = readFromStream(is);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (is != null)
                is.close();
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream is) throws IOException {
        StringBuilder output = new StringBuilder();
        if (is != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private class GetToiletsAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            String jsonResponse = "";
            try {
                url = new URL(strings[0]);
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject root = null;
            try {
                root = new JSONObject(s);
                JSONArray toilets = root.getJSONArray("features");


                    nuOfToilets=toilets.length();
                    for(int i=0;i<nuOfToilets;i++)
                    {
                        JSONObject firstToilet = toilets.getJSONObject(i);
                        JSONObject geometry = firstToilet.getJSONObject("geometry");
                        JSONArray coor=geometry.getJSONArray("coordinates");
                        JSONObject properties=firstToilet.getJSONObject("properties");
                        String status=properties.getString("Status");
                        String address=properties.getString("Adresse");
                        double latitude=coor.optDouble(0);
                        double longitude=coor.optDouble(1);
                        toiletsInAarhus.add(new Toilet(status,address,longitude,latitude));
                        toiletsMarkers.add(new MarkerOptions().position(new LatLng(longitude,latitude)).title(address));
                    }



                //text2.setText(Double.toString(toilet.getLatitude()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
