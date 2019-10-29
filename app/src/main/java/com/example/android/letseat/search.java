package com.example.android.letseat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class search extends AppCompatActivity {

    /**
     * This activity should let the users more specifically choose what they want
     * - provide filters to search for certain categories of food,
     * - allow certain types of food to not appear (ie, no mexican food)
     * - search for restaurant names
     * -
     */

    private String LOG_TAG = search.class.getSimpleName();

    private ArrayList<Business> bArray = new ArrayList<Business>();

    private static final int LOCATION_REQUEST_CODE = 1000;
    private Location myLocation;

    private View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            executeSearch();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button myButton = findViewById(R.id.searchButton);

        myButton.setOnClickListener(searchListener);

        getLocation();

    }

    /**
     * Button Execution
     */
    private void executeSearch() {
        Intent startListActivity = new Intent(this, BusinessListView.class);

        startListActivity.putParcelableArrayListExtra("DATA", bArray);

        startActivity(startListActivity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            // If the permission is granted, get the location,
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Log.d(LOG_TAG, "Failed to get permissions");
            }
        }
    }

    /**
     * Retrieves Location and stores in myLocation variable
     * Once location is retrieved, fires off the Async Task for API call
     */
    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Log.d(LOG_TAG, "getLocation: permissions granted");
        }

        FusedLocationProviderClient myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = myFusedLocationClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                    Log.d(LOG_TAG, "My location is " + myLocation.toString());
                } else {
                    Log.d(LOG_TAG, "Location finding error: NULL retrieved");
                }
            }
        });

        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //get data from yelp api
                FetchDataAsyncTask fetchDataTask = new FetchDataAsyncTask();
                fetchDataTask.execute();
            }
        });

    }

}
