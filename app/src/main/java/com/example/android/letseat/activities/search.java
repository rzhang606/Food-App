package com.example.android.letseat.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.widget.Button;

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

    private View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            executeSearch();
        }
    };
    public FetchAPIData apiDataFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        super.setNavigationListener();

        Button myButton = findViewById(R.id.searchButton);
        myButton.setOnClickListener(searchListener);

        FragmentManager fragmentManager = getSupportFragmentManager();

        apiDataFetcher = new FetchAPIData(this, this);
        apiDataFetcher.apiDelegate = this;
        apiDataFetcher.search("");


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
    public void apiResponse(ArrayList<Business> bArr) {
        bArray = bArr;
    }
}
