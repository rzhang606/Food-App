package com.example.android.letseat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
    EditText searchQuery;
    Button searchButton;
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

        //Set up search
        searchQuery = findViewById(R.id.list_search_query);
        searchButton = findViewById(R.id.list_search_button);
        setUpSearch(this, searchQuery, searchButton);


    }

    //Expands the search/filter text fields

    /**
     * Switches between expanded and half
     */
    private void toggleFilters() {
        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState((BottomSheetBehavior.STATE_HALF_EXPANDED));
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    /**
     * Sets up the bottom sheet (which contains the list)
     * @param coordLayoutID : coordinator layout (aka root element)
     * @param contentID : the content of the dropdown ( the list view and its friends)
     */
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

    /**
     * Sets up the list! Sets a item click listener and the footer view along with the scroll listener to load more upon scrolling to the bottom
     */
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

        View footerView = getLayoutInflater().inflate(R.layout.list_view_footer, null, false);
        myListView.addFooterView(footerView);


        myListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE && myListView.getLastVisiblePosition() == bArray.size()-1) {
                    findViewById(R.id.list_progress_bar).setVisibility(View.VISIBLE);
                    //add more items
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
        });
    }

    /**
     * Sets up the spinner (dropdown) to allow sorting
     * @param spinner : the spinner view
     */
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
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    /**
     * Sets up the search function by creating a listener for the button press and grabbing the query
     * @param context : activity context
     * @param sQuery : query string from user
     * @param sButton : the apply search button
     */
    private void setUpSearch(final Context context, final EditText sQuery, Button sButton) {
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = sQuery.getText().toString();
                Intent searchIntent = new Intent(context, search.class);
                searchIntent.putExtra("List", query);
                startActivity(searchIntent);
            }
        });
    }

    /**
     * Provides sorting functionality for the bArray
     * @param bArr : the array of businesses
     * @param criteria : what the search criteria is
     * @return : the sorted list
     */
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
