package com.example.android.letseat.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.letseat.BottomNavigationActivity;
import com.example.android.letseat.Business;
import com.example.android.letseat.utility.BusinessAdapter;
import com.example.android.letseat.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class BusinessListView extends BottomNavigationActivity {

    private final String LOG_TAG = BusinessListView.class.getSimpleName();
    BottomSheetBehavior sheetBehavior;
    ListView myListView;
    ArrayList<Business> bArray = new ArrayList<>();


    /**
     * Fetches data from source (search.java) and instantiates the adapter for the listview
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list_view);

        super.setNavigationListener(); // sets bottom nav bar

        //Grab values from the search results
        Intent intent = getIntent();
        bArray = intent.getParcelableArrayListExtra("DATA");

        myListView = findViewById(R.id.myListView);
        //Set adapter for the list
        setUpList();

        //Set up the bottom sheet
        setUpBottomSheet(R.id.list_coordinator_layout, R.id.list_content_layout);


    }

    //Expands the search/filter text fields
    private void toggleFilters() {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState((BottomSheetBehavior.STATE_HALF_EXPANDED));
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void setUpBottomSheet(int coordLayoutID, int contentID) {
        CoordinatorLayout coordinatorLayout = findViewById(coordLayoutID);
        LinearLayout contentLayout = coordinatorLayout.findViewById(contentID);

        sheetBehavior = BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetBehavior.setHideable(false);

        //Pressing the filter icon opens up the search option and filters
        ImageView filterIcon = coordinatorLayout.findViewById(R.id.filterIcon);
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFilters();
            }
        });
    }

    private void setUpList(){

        //Adapter for each row
        BusinessAdapter arrayAdapter = new BusinessAdapter(this, R.layout.business_row, bArray);
        myListView.setAdapter(arrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                Log.d(LOG_TAG, view.toString());
                bundle.putParcelable("BUSINESS", bArray.get(position));

                Intent intent = new Intent(BusinessListView.this, surpriseMe.class);
                intent.putExtra("Bundle", bundle);
                intent.putExtra("FROM_MAIN", 0);

                startActivity(intent);
            }
        });
    }

}
