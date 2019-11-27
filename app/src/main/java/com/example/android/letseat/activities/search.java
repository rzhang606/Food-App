package com.example.android.letseat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.android.letseat.APIDataResponse;
import com.example.android.letseat.BottomNavigationActivity;
import com.example.android.letseat.Business;
import com.example.android.letseat.utility.FetchAPIData;
import com.example.android.letseat.R;

import java.util.ArrayList;

public class search extends BottomNavigationActivity implements APIDataResponse {

    /**
     * This activity should let the users more specifically choose what they want
     * - provide filters to search for certain categories of food,
     * - allow certain types of food to not appear (ie, no mexican food)
     * - search for restaurant names
     */

    private static String LOG_TAG = search.class.getSimpleName();
    private ArrayList<Business> bArray = new ArrayList<Business>();

    ProgressBar progressBar;
    Button searchButton;
    SearchView searchView;

    @Override
    protected void onStop() {
        super.onStop();
        if(progressBar != null){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            executeSearch(searchView.getQuery().toString(), 0);
        }
    };

    public FetchAPIData apiDataFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        super.setNavigationListener();

        //UI Elements
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(searchListener);
        progressBar = findViewById(R.id.s_progressBar);
        searchView = findViewById(R.id.s_search);

        //
        apiDataFetcher = new FetchAPIData(this, this);
        apiDataFetcher.apiDelegate = this;

        //Check for redirection from list search
        Intent intent = getIntent();
        String listExtra = intent.getStringExtra("List");
        int offsetExtra = intent.getIntExtra("List_Offset", 0);
        if(listExtra != null) {
            executeSearch(listExtra, offsetExtra);
        }
    }

    /**
     * Button Execution to search
     */
    private void executeSearch(String searchQuery, int offset) {

        Log.d(LOG_TAG, "Words: " + searchQuery);

        progressBar.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);

        apiDataFetcher.search(searchQuery, offset);
    }

    /**
     * Runs after completion of background task
     * @param bArr
     */
    @Override
    public void apiResponse(ArrayList<Business> bArr) {
        bArray = bArr;

        Intent startListActivity = new Intent(this, BusinessListView.class);

        startListActivity.putParcelableArrayListExtra("DATA", bArray);
        startActivity(startListActivity);
    }
}
