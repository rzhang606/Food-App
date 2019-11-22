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
import android.widget.Spinner;

import com.example.android.letseat.BottomNavigationActivity;
import com.example.android.letseat.Business;
import com.example.android.letseat.utility.BusinessAdapter;
import com.example.android.letseat.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class BusinessListView extends BottomNavigationActivity {

    private final String LOG_TAG = BusinessListView.class.getSimpleName();
    BottomSheetBehavior sheetBehavior;
    Spinner spinner;
    int currentSpinnerItem = 0;
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

        //Spinner
        spinner = findViewById(R.id.list_spinner);
        setUpSpinner(spinner);



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

    private void setUpSpinner(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == currentSpinnerItem) {
                    return;
                }

                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if(selectedItem.equals("Distance")) {
                    bArray = sortList(bArray, "distance");
                } else if (selectedItem.equals("Price")) {
                    bArray = sortList(bArray, "price");
                }
                setUpList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private ArrayList<Business> sortList(ArrayList<Business> bArr, String criteria){
        Comparator<Business> comparator;
        if(criteria.equals("distance")) {
            comparator = new Comparator<Business>() {
                @Override
                public int compare(Business business, Business t1) {
                    double b1 = business.getDistance();
                    double b2 = t1.getDistance();
                    if(b1 > b2){
                        return 1;
                    } else if (b1 < b2){
                        return -1;
                    }
                    return 0;
                }
            };
        } else { //price
            comparator = new Comparator<Business>() {
                @Override
                public int compare(Business business, Business t1) {
                    int len1 = business.getPrice().length();
                    int len2 = t1.getPrice().length();
                    if(len1 > len2) {
                        return 1;
                    } else if (len1 < len2){
                        return -1;
                    }
                    return 0;
                }
            };
        }

        Collections.sort(bArr, comparator);

        return bArr;

    }
}
