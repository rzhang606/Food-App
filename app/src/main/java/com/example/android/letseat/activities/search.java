package com.example.android.letseat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.android.letseat.interfaces.APIDataResponse;
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
    public FetchAPIData apiDataFetcher;

    ProgressBar progressBar;
    Button searchButton;
    SearchView searchView;

    @Override
    protected void onStart() {
        super.onStart();
        searchButton.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

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
    }

    /**
     * Listener
     */
    private View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            executeSearch(searchView.getQuery().toString());
        }
    };

    /**
     * Button Execution to search
     */
    private void executeSearch(String searchQuery) {

        Log.d(LOG_TAG, "Words: " + searchQuery);

        progressBar.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);

        apiDataFetcher.search(searchQuery);
    }

    /**
     * Runs after completion of background task
     * @param bArray : business array
     */
    @Override
    public void apiResponse(ArrayList<Business> bArray, String query) {

        Intent startListActivity = new Intent(this, BusinessListView.class);

        startListActivity.putParcelableArrayListExtra("DATA", bArray);
        startListActivity.putExtra("QUERY", query);
        startActivity(startListActivity);
    }
}
