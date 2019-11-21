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

import com.example.android.letseat.APIDataResponse;
import com.example.android.letseat.AsyncResponse;
import com.example.android.letseat.BottomNavigationActivity;
import com.example.android.letseat.Business;
import com.example.android.letseat.fragments.BusinessDisplayFragment;
import com.example.android.letseat.utility.FetchAPIData;
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

public class surpriseMe extends BottomNavigationActivity implements APIDataResponse {

    private static final String LOG_TAG = surpriseMe.class.getSimpleName();

    private ArrayList<Business> bArray = new ArrayList<>();
    BusinessDisplayFragment mFrag;

    FetchAPIData apiDataFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise_me);
        super.setNavigationListener();

        Button button = (Button) findViewById(R.id.b_frag_another);
        findAnotherListener(button);

        Intent intent = getIntent();
        int fromMain = intent.getIntExtra("FROM_MAIN", 1);

        mFrag = (BusinessDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.sm_fragment);

        if (fromMain == 0) { //create the fragment from the search page
            Business mBusiness = intent.getBundleExtra("Bundle").getParcelable("BUSINESS");
            mFrag.Initialize(mBusiness);
            ProgressBar mProgressBar = findViewById(R.id.sm_progressBar);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            apiDataFetcher = new FetchAPIData(this, this);
            apiDataFetcher.apiDelegate = this;
            apiDataFetcher.search("");
        }

    }

    public void findAnotherListener(Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                int myRand = rand.nextInt(20); //between 0-19

                mFrag = (BusinessDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.sm_fragment);
                try {
                    mFrag.Initialize(bArray.get(myRand));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void apiResponse(ArrayList<Business> bArr) {
        bArray = bArr;

        Random rand = new Random();
        int myRand = rand.nextInt(20);
        mFrag.Initialize(bArr.get(myRand));
    }
}
