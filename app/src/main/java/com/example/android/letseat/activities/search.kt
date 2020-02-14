package com.example.android.letseat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView

import com.example.android.letseat.interfaces.APIDataResponse
import com.example.android.letseat.BottomNavigationActivity
import com.example.android.letseat.Business
import com.example.android.letseat.utility.FetchAPIData
import com.example.android.letseat.R

import java.util.ArrayList

class search : BottomNavigationActivity(), APIDataResponse {
    private lateinit var apiDataFetcher: FetchAPIData

    private lateinit var progressBar: ProgressBar
    private lateinit var searchButton: Button
    private lateinit var searchView: SearchView

    /**
     * Listener
     */
    private val searchListener = View.OnClickListener { executeSearch(searchView.query.toString()) }

    override fun onStart() {
        super.onStart()
        searchButton.visibility = View.VISIBLE
        searchView.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        super.setNavigationListener()

        //UI Elements
        searchButton = findViewById(R.id.searchButton)
        searchButton.setOnClickListener(searchListener)
        progressBar = findViewById(R.id.s_progressBar)
        searchView = findViewById(R.id.s_search)

        //
        apiDataFetcher = FetchAPIData(this, this)
        apiDataFetcher.apiDelegate = this
    }

    /**
     * Button Execution to search
     */
    private fun executeSearch(searchQuery: String) {

        Log.d(LOG_TAG, "Words: $searchQuery")

        progressBar.visibility = View.VISIBLE
        searchView.visibility = View.INVISIBLE
        searchButton.visibility = View.INVISIBLE

        apiDataFetcher.search(searchQuery)
    }

    /**
     * Runs after completion of background task
     * @param bArray : business array
     */
    override fun apiResponse(bArray: ArrayList<Business>, query: String) {

        val startListActivity = Intent(this, BusinessListView::class.java)

        startListActivity.putParcelableArrayListExtra("DATA", bArray)
        startListActivity.putExtra("QUERY", query)
        startActivity(startListActivity)
    }

    companion object {

        /**
         * This activity should let the users more specifically choose what they want
         * - provide filters to search for certain categories of food,
         * - allow certain types of food to not appear (ie, no mexican food)
         * - search for restaurant names
         */

        private val LOG_TAG = search::class.java.simpleName
    }
}
