package com.example.android.letseat.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.android.letseat.AsyncResponse;
import com.example.android.letseat.BottomNavigationActivity;
import com.example.android.letseat.Business;
import com.example.android.letseat.fragments.BusinessDisplayFragment;
import com.example.android.letseat.utility.FetchDataAsyncTask;
import com.example.android.letseat.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class surpriseMe extends BottomNavigationActivity implements AsyncResponse {

    private static final String LOG_TAG = surpriseMe.class.getSimpleName();
    private static final int LOCATION_REQUEST_CODE = 1000;

    private Location myLocation;
    private ArrayList<Business> bArray = new ArrayList<>();
    private FetchDataAsyncTask fetchDataTask = new FetchDataAsyncTask();
    BusinessDisplayFragment mFrag;

    public void processResult(JSONArray output) {
        //receives result of async of onPostExecute
        Log.d(LOG_TAG, "OUTPUT: " + output.toString());

        try{
            //process JSONArray to usable arraylist
            for(int i = 0; i < output.length(); i++){
                insertBusiness((JSONObject)output.get(i));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "ERROR: JSON Exception");
            e.printStackTrace();
        }

        Random rand = new Random();
        int myRand = rand.nextInt(20); //between 0-19

        mFrag.Initialize(bArray.get(myRand));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise_me);
        super.setNavigationListener();

        Button button = (Button)findViewById(R.id.b_frag_another);
        findAnotherListener(button);

        Intent intent = getIntent();
        int fromMain = intent.getIntExtra("FROM_MAIN", 1);

        mFrag = (BusinessDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.sm_fragment);
        if(fromMain == 0){ //create the fragment from the search page
            Business mBusiness = intent.getBundleExtra("Bundle").getParcelable("BUSINESS");
            mFrag.Initialize(mBusiness);
            ProgressBar mProgressBar = findViewById(R.id.sm_progressBar);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            //gets location and creates the fragment
            fetchDataTask.delegate = this;
            Task<Location> task = getLocation();

            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        myLocation = location;
                        try {
                            URL generalURL = new URL(getString(R.string.myLocationSearch) + "?latitude=" + myLocation.getLatitude() + "&longitude=" + myLocation.getLongitude());
                            Log.d(LOG_TAG, "URL created: " + generalURL.toString());

                            //get data from yelp api
                            fetchDataTask.execute(generalURL);
                        } catch (MalformedURLException e) {
                            Log.d(LOG_TAG, "URL Malformed");
                        }

                    } else {
                        Log.d(LOG_TAG, "Location finding error: NULL retrieved");
                    }
                }
            });

        }

    }

    public void findAnotherListener(Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                int myRand = rand.nextInt(20); //between 0-19

                mFrag = (BusinessDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.sm_fragment);
                try{ mFrag.Initialize(bArray.get(myRand)); }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
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
     */
    private Task<Location> getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Log.d(LOG_TAG, "getLocation: permissions granted");
        }

        FusedLocationProviderClient myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = myFusedLocationClient.getLastLocation();

        return task;

    }

    /**
     * Takes a jsonobject representing a business and creates an instance of our
     * business class, then inserts into the hashmap
     *
     * @param obj   - the json object to be evaluated
     */
    private void insertBusiness(JSONObject obj) {
        //get all categories from json
        List<String> categories = new ArrayList<>();
        try {
            //categories
            JSONArray jsonCategories = obj.getJSONArray("categories");
            for (int i = 0; i < jsonCategories.length(); i++) {
                categories.add(jsonCategories.getJSONObject(i).getString("title"));
            }

            //location
            JSONArray jsonLocation = obj.getJSONObject("location").getJSONArray("display_address");
            String location = jsonLocation.toString();
            //Log.d(LOG_TAG, "NAME OF LOCATION: " + location);


            //construct new business and put into the map
            Business business = new Business(
                    obj.getString("name"),
                    obj.getBoolean("is_closed"),
                    obj.getString("image_url"),
                    categories,
                    obj.getInt("rating"),
                    location,
                    obj.getString("phone"),
                    obj.getDouble("distance"),
                    obj.getString("price")
            );
            Log.d(LOG_TAG, "PRICE: " + business.getPrice());
            bArray.add(business);
            Log.d(LOG_TAG, "Businesses Count: " + bArray.size());
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Insert business failed...");
            e.printStackTrace();
        }

    }
}
