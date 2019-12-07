package com.example.android.letseat.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.android.letseat.BottomNavigationActivity
import com.example.android.letseat.Business
import com.example.android.letseat.R
import com.example.android.letseat.interfaces.APIDataResponse
import com.example.android.letseat.utility.FetchAPIData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

class MapsActivity : BottomNavigationActivity(), OnMapReadyCallback, APIDataResponse {

    private lateinit var mMap: GoogleMap

    //UI Elements
    private lateinit var searchText : EditText
    private lateinit var searchButton : Button
    private lateinit var sheetBehavior : BottomSheetBehavior<LinearLayout>

    //For search
    private lateinit var apiDataFetcher : FetchAPIData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        super.setNavigationListener()

        //Grab UI Elements
        searchText = findViewById(R.id.map_search_query)
        searchButton = findViewById(R.id.map_search_button)

        //Search Set up
        apiDataFetcher = FetchAPIData(this, this)
        apiDataFetcher.apiDelegate = this
        setUpSearch( searchText, searchButton)

        //Bottom Sheet Setup
        setUpBottomSheet(R.id.map_coordinator_layout, R.id.map_content_layout)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    /**
     * Sets up the search function by creating a listener for the button press and grabbing the query
     * @param context : activity context
     * @param sQuery : query string from user
     * @param sButton : the apply search button
     */
    private fun setUpSearch(sQuery: EditText, sButton: Button) {
        sButton.setOnClickListener {
            val query = sQuery.text.toString()
            //Set UI
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            //Execute search
            apiDataFetcher.search(query)
        }
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
        val filterIcon = findViewById<ImageView>(R.id.map_filterIcon)
        filterIcon.setOnClickListener( View.OnClickListener { toggleFilters() })
    }

    /**
     * Response of the search
     * @param bArr : values fetched
     */
    override fun apiResponse(bArr: ArrayList<Business?>, query: String) {
        //set markers

        //New task instance must be created
        apiDataFetcher = FetchAPIData(this, this)
        apiDataFetcher.apiDelegate = this
    }

    /**
     * Sets a single marker
     * @param business : the business to set as a marker
     */
    private fun setMarker(business : Business) {
        mMap.addMarker(MarkerOptions().position(   ).title(business.name))
    }

    /**
     * Sets the array of businesses as markers
     * @param bArr : Array of businesses
     */
    private fun setMarkerArray(bArr : ArrayList<Business>) {
        for (item in bArr) {
            setMarker(item)
        }
    }

    /**
     * Sets the information window for a single business
     */
    private fun setInfoWindow(business: Business) {

    }
}
