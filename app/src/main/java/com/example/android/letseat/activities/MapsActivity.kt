package com.example.android.letseat.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.android.letseat.BottomNavigationActivity

import com.example.android.letseat.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MapsActivity : BottomNavigationActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    //UI Elements
    lateinit var searchText : EditText
    lateinit var searchButton : Button
    lateinit var sheetBehavior : BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        super.setNavigationListener()

        //Grab UI Elements
        searchText = findViewById(R.id.map_search_query)
        searchButton = findViewById(R.id.map_search_button)

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
        sheetBehavior.setFitToContents(false)
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        sheetBehavior.setHideable(false)

        //Pressing the filter icon opens up the search option and filters
        val filterIcon = findViewById<ImageView>(R.id.map_filterIcon)
        filterIcon.setOnClickListener( View.OnClickListener { toggleFilters() })
    }
}
