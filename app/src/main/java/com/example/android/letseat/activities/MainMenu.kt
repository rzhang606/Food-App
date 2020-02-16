package com.example.android.letseat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.example.android.letseat.R

class MainMenu : AppCompatActivity() {

    //Companion object is tied to the class
    companion object {
        private val LOG_TAG = MainMenu::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
    }

    /**
     * Launches the SurpriseMe activity
     *
     * @param view
     */
    fun surpriseMeButton(view: View) {
        Log.d(LOG_TAG, "Surprise Me clicked")
        val intent = Intent(this, surpriseMe::class.java)
        startActivity(intent)
    }

    /**
     * Search for food manually
     *
     * @param view
     */
    fun searchButton(view: View) {
        Log.d(LOG_TAG, "Search buttib clicked")
        val intent = Intent(this, search::class.java)
        startActivity(intent)
    }

    /**
     * Maps Activity
     */

    fun mapsButton(view : View) {
        Log.d(LOG_TAG, "Lauched maps activity")
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }


}
