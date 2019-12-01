package com.example.android.letseat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.android.letseat.APIDataResponse;
import com.example.android.letseat.BottomNavigationActivity;
import com.example.android.letseat.Business;
import com.example.android.letseat.fragments.BusinessDisplayFragment;
import com.example.android.letseat.utility.FetchAPIData;
import com.example.android.letseat.R;

import java.util.ArrayList;
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

        Button button = findViewById(R.id.b_frag_another);
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
                    Log.e(LOG_TAG, "Error attaching the get another button");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void apiResponse(ArrayList<Business> bArr, String query) {
        bArray = bArr;

        Random rand = new Random();
        int myRand = rand.nextInt(20);
        mFrag.Initialize(bArr.get(myRand));
    }
}
