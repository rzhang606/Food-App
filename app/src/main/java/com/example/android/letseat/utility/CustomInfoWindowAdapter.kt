package com.example.android.letseat.utility

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.android.letseat.Business
import com.example.android.letseat.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import java.lang.ref.WeakReference


class CustomInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {

    private val weakContext = WeakReference(context)

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        //return null
        Log.d("InfoWindow", "Setting up window")
        val context = weakContext.get()
        val layoutInflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.infowindow, null) //null root is ok here, this is for a marker window

        val business = marker.tag as Business

        val textView : TextView = view.findViewById(R.id.if_name)
        textView.text = business.name

        Log.d("InfoWindow", view.toString())

        return view
    }
}