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
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.android.letseat.APIDataResponse;
import com.example.android.letseat.BottomNavigationActivity;
import com.example.android.letseat.Business;
import com.example.android.letseat.utility.BusinessAdapter;
import com.example.android.letseat.R;
import com.example.android.letseat.utility.FetchAPIData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BusinessListView extends BottomNavigationActivity implements APIDataResponse {

    private final String LOG_TAG = BusinessListView.class.getSimpleName();

    //UI Elements
    BottomSheetBehavior sheetBehavior;
    Spinner spinner;
    ListView myListView;
    EditText searchQuery;
    Button searchButton;
    ProgressBar bigProgressBar;

    ArrayList<Business> bArray = new ArrayList<>();

    int currentSpinnerItem = 0;

    FetchAPIData apiDataFetcher;

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

        //Set adapter for the list
        myListView = findViewById(R.id.myListView);
        setUpList();

        //Set up the bottom sheet
        setUpBottomSheet(R.id.list_coordinator_layout, R.id.list_content_layout);

        //Spinner (aka dropdown menu)
        spinner = findViewById(R.id.list_spinner);
        setUpSpinner(spinner);

        //Set up search
        bigProgressBar = findViewById(R.id.list_big_progressBar);
        apiDataFetcher = new FetchAPIData(this, this);
        apiDataFetcher.apiDelegate = this;

        searchQuery = findViewById(R.id.list_search_query);
        searchButton = findViewById(R.id.list_search_button);
        setUpSearch(this, searchQuery, searchButton);


    }

    /**
     * Switches between expanded and half, bottom sheet locked otherwise
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

        //FooterView and the scroll listener handles loading more upon reaching bottom of list
        View footerView = getLayoutInflater().inflate(R.layout.list_view_footer, null, false);
        myListView.addFooterView(footerView);

        myListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE && myListView.getLastVisiblePosition() == bArray.size()-1) {
                    findViewById(R.id.list_progress_bar).setVisibility(View.VISIBLE);
                    // TODO:
                    //add more items
                    //preserve query is needed for this function
                    //executing search with extra List_Offset with the value of the current size of bArr performs the search for new stuff
                    //needs to add that on top of the current array and pass it back through
                    //how to also maintain the current list?

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
                setUpList(); //TODO: set up list isnt the cheapest operation, need to handle this better
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
                //Set UI
                bigProgressBar.setVisibility(View.VISIBLE);
                myListView.setVisibility(View.INVISIBLE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                //Execute search
                apiDataFetcher.search(query);
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

    /**
     * Response of the search
     * @param bArr : values fetched
     */
    @Override
    public void apiResponse(ArrayList<Business> bArr) {
        bArray = bArr;
        setUpList();
        myListView.setVisibility(View.VISIBLE);
        bigProgressBar.setVisibility(View.INVISIBLE);

        //New task instance must be created
        apiDataFetcher = new FetchAPIData(this, this);
        apiDataFetcher.apiDelegate = this;
    }
}
