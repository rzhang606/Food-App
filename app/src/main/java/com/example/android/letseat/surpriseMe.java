package com.example.android.letseat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class surpriseMe extends FragmentActivity {

    private static final String LOG_TAG = surpriseMe.class.getSimpleName();
    private static final int LOCATION_REQUEST_CODE = 1000;
    private Location myLocation;

    //holds businesses objects
    ArrayList<Business> bArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise_me);

        Intent intent = getIntent();
        int fromMain = intent.getIntExtra("FROM_MAIN", 1);

        if(fromMain == 0){ //create the fragment from the search page
            BusinessDisplayFragment myFrag = (BusinessDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.sm_fragment);
            Business mBusiness = intent.getBundleExtra("Bundle").getParcelable("BUSINESS");
            myFrag.Initialize(mBusiness);
            ProgressBar mProgressBar = findViewById(R.id.sm_progressBar);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            //gets location and creates the fragment
            //TODO: Separate the logic for creating fragment, so that getLocation only gets location, and then create fragment later
            getLocation();
        }

    }

    public void findAnother(View view) {

        Random rand = new Random();
        int myRand = rand.nextInt(20); //between 0-19

        BusinessDisplayFragment myFrag = (BusinessDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.sm_fragment);
        myFrag.Initialize(bArray.get(myRand));
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
                //Create URL
                URL generalURL = null;
                try {
                    generalURL = new URL(getString(R.string.myLocationSearch) + "?latitude=" + myLocation.getLatitude() + "&longitude=" + myLocation.getLongitude());
                    Log.d(LOG_TAG, "URL created: " + generalURL.toString());
                } catch (NullPointerException e) {
                    Log.d(LOG_TAG, "mylocation is null");
                } catch (MalformedURLException e) {
                    Log.d(LOG_TAG, "URL Malformed");
                }

                //get data from yelp api
                FetchDataAsyncTask fetchDataTask = new FetchDataAsyncTask();
                fetchDataTask.execute(generalURL);
            }
        });

    }
}
