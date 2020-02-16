package com.example.android.letseat.activities

import android.content.Intent
import android.os.Bundle

import androidx.coordinatorlayout.widget.CoordinatorLayout

import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner

import com.example.android.letseat.BottomNavigationActivity
import com.example.android.letseat.Business
import com.example.android.letseat.interfaces.APIDataResponse
import com.example.android.letseat.utility.BusinessAdapter
import com.example.android.letseat.R
import com.example.android.letseat.utility.FetchAPIData
import com.google.android.material.bottomsheet.BottomSheetBehavior

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

class BusinessListView : BottomNavigationActivity(), APIDataResponse {

    private val LOG_TAG = BusinessListView::class.java.simpleName
    private var footerSetUp = false
    private val currentSpinnerItem = 0

    //UI Elements
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var spinner: Spinner
    private lateinit var myListView: ListView
    private lateinit var searchQuery: EditText
    private lateinit var searchButton: Button
    private lateinit var bigProgressBar: ProgressBar
    private lateinit var listProgressBar: ProgressBar
    private lateinit var arrayAdapter: BusinessAdapter

    //Data
    private var bArray = ArrayList<Business>()
    private lateinit var query: String

    //Utility
    private lateinit var apiDataFetcher: FetchAPIData

    /**
     * Fetches data from source (search.java) and instantiates the adapter for the listview
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_list_view)
        super.setNavigationListener() // sets bottom nav bar

        //Grab values from the search results
        val intent = intent
        bArray = intent.getParcelableArrayListExtra("DATA")
        query = intent.getStringExtra("QUERY")

        //Set adapter for the list
        myListView = findViewById(R.id.myListView)
        setUpList()

        //Set up the bottom sheet
        setUpBottomSheet(R.id.list_coordinator_layout, R.id.list_content_layout)

        //Spinner (aka dropdown menu)
        spinner = findViewById(R.id.list_spinner)
        setUpSpinner(spinner)

        //Set up search
        bigProgressBar = findViewById(R.id.list_big_progressBar)
        apiDataFetcher = FetchAPIData(this, this)
        apiDataFetcher.apiDelegate = this

        searchQuery = findViewById(R.id.list_search_query)
        searchButton = findViewById(R.id.list_search_button)
        setUpSearch(searchQuery, searchButton)


    }

    /**
     * Switches between expanded and half, bottom sheet locked otherwise
     */
    private fun toggleFilters() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    /**
     * Sets up the bottom sheet (which contains the list)
     * @param coordLayoutID : coordinator layout (aka root element)
     * @param contentID : the content of the dropdown ( the list view and its friends)
     */
    private fun setUpBottomSheet(coordLayoutID: Int, contentID: Int) {
        val coordinatorLayout = findViewById<CoordinatorLayout>(coordLayoutID)
        val contentLayout = coordinatorLayout.findViewById<LinearLayout>(contentID)

        sheetBehavior = BottomSheetBehavior.from(contentLayout)
        sheetBehavior.isFitToContents = false
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        //Pressing the filter icon opens up the search option and filters
        val filterIcon = coordinatorLayout.findViewById<ImageView>(R.id.filterIcon)
        filterIcon.setOnClickListener { toggleFilters() }
    }

    /**
     * Sets up the list! Sets a item click listener and the footer view along with the scroll listener to load more upon scrolling to the bottom
     */
    private fun setUpList() {

        //Adapter for each row
        arrayAdapter = BusinessAdapter(this, R.layout.business_row, bArray)
        myListView.adapter = arrayAdapter

        myListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bundle = Bundle()
            Log.d(LOG_TAG, view.toString())
            bundle.putParcelable("BUSINESS", bArray[position])

            val intent = Intent(this@BusinessListView, surpriseMe::class.java)
            intent.putExtra("Bundle", bundle)
            intent.putExtra("FROM_MAIN", 0)

            startActivity(intent)
        }

        if (!footerSetUp) {
            setUpFooter()
        }
    }

    /**
     * Sets up bottom footer (loading circle) , and the logic for loading more items
     */
    private fun setUpFooter() {
        //FooterView and the scroll listener handles loading more upon reaching bottom of list
        val footerView = layoutInflater.inflate(R.layout.list_view_footer, null, false)
        myListView.addFooterView(footerView, null, false)

        myListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && myListView.lastVisiblePosition >= bArray.size - 1) {
                    listProgressBar = findViewById(R.id.list_progress_bar)
                    listProgressBar.visibility = View.VISIBLE
                    Log.d(LOG_TAG, "Searching more")
                    //Search more
                    apiDataFetcher.search(query, bArray.size)
                    Log.d(LOG_TAG, "Search More Executed ... ")
                }
            }

            override fun onScroll(absListView: AbsListView, i: Int, i1: Int, i2: Int) {}
        })
    }

    /**
     * Sets up the spinner (dropdown) to allow sorting
     * @param spinner : the spinner view
     */
    private fun setUpSpinner(spinner: Spinner) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                if (i == currentSpinnerItem) {
                    return
                }

                val selectedItem = adapterView.getItemAtPosition(i).toString()
                if (selectedItem == "Distance") {
                    bArray = sortList(bArray, "distance")
                } else if (selectedItem == "Price") {
                    bArray = sortList(bArray, "price")
                }
                setUpList() //TODO: set up list isnt the cheapest operation, need to handle this better
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    /**
     * Sets up the search function by creating a listener for the button press and grabbing the query
     * @param sQuery : query string from user
     * @param sButton : the apply search button
     */
    private fun setUpSearch(sQuery: EditText, sButton: Button) {
        sButton.setOnClickListener {
            val query = sQuery.text.toString()
            //Set UI
            bigProgressBar.visibility = View.VISIBLE
            myListView.visibility = View.INVISIBLE
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            footerSetUp = true
            //Execute search
            apiDataFetcher.search(query)
        }
    }

    /**
     * Provides sorting functionality for the bArray
     * @param bArr : the array of businesses
     * @param criteria : what the search criteria is
     * @return : the sorted list
     */
    private fun sortList(bArr: ArrayList<Business>, criteria: String): ArrayList<Business> {
        val comparator: Comparator<Business>
        if (criteria == "distance") {
            comparator = Comparator { business, t1 ->
                val b1 = business.distance
                val b2 = t1.distance
                if (b1 > b2) {
                    return@Comparator 1
                } else if (b1 < b2) {
                    return@Comparator -1
                }
                0
            }
        } else { //price
            comparator = Comparator { business, t1 ->
                val len1 = business.price.length
                val len2 = t1.price.length
                if (len1 > len2) {
                    return@Comparator 1
                } else if (len1 < len2) {
                    return@Comparator -1
                }
                0
            }
        }

        Collections.sort(bArr, comparator)

        return bArr

    }

    /**
     * Response of the search
     * @param bArr : values fetched
     */
    override fun apiResponse(bArr: ArrayList<Business>, query: String) {

        if (this.query == query) { //Load more
            bArray.addAll(bArr)
            listProgressBar.visibility = View.GONE
        } else { //First time searching this query
            this.query = query
            bArray = bArr
            setUpList()
        }

        arrayAdapter.notifyDataSetChanged()

        myListView.visibility = View.VISIBLE
        bigProgressBar.visibility = View.INVISIBLE

        //New task instance must be created
        apiDataFetcher = FetchAPIData(this, this)
        apiDataFetcher.apiDelegate = this
    }
}
