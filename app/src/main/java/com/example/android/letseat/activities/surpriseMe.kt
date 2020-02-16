package com.example.android.letseat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar

import com.example.android.letseat.BottomNavigationActivity
import com.example.android.letseat.Business
import com.example.android.letseat.fragments.BusinessDisplayFragment
import com.example.android.letseat.interfaces.APIDataResponse
import com.example.android.letseat.utility.FetchAPIData
import com.example.android.letseat.R

import java.util.ArrayList
import java.util.Random

class surpriseMe : BottomNavigationActivity(), APIDataResponse {

    private var bArray = ArrayList<Business>()
    private lateinit var mFrag: BusinessDisplayFragment

    private lateinit var apiDataFetcher: FetchAPIData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surprise_me)
        super.setNavigationListener()

        val button = findViewById<Button>(R.id.b_frag_another)
        findAnotherListener(button)

        val intent = intent
        val fromMain = intent.getIntExtra("FROM_MAIN", 1)

        mFrag = supportFragmentManager.findFragmentById(R.id.sm_fragment) as BusinessDisplayFragment

        if (fromMain == 0) { //create the fragment from the search page
            val mBusiness = intent.getBundleExtra("Bundle").getParcelable<Business>("BUSINESS")
            mFrag.Initialize(mBusiness)
            val mProgressBar = findViewById<ProgressBar>(R.id.sm_progressBar)
            mProgressBar.visibility = View.INVISIBLE
        } else {
            apiDataFetcher = FetchAPIData(this, this)
            apiDataFetcher.apiDelegate = this
            apiDataFetcher.search("")
        }

    }

    fun findAnotherListener(button: Button) {

        button.setOnClickListener {
            val rand = Random()
            val myRand = rand.nextInt(20) //between 0-19

            mFrag = supportFragmentManager.findFragmentById(R.id.sm_fragment) as BusinessDisplayFragment
            try {
                mFrag.Initialize(bArray[myRand])
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error attaching the get another button")
                e.printStackTrace()
            }
        }
    }

    override fun apiResponse(bArr: ArrayList<Business>, query: String) {
        bArray = bArr

        val rand = Random()
        val myRand = rand.nextInt(20)
        mFrag.Initialize(bArr[myRand])
    }

    companion object {

        private val LOG_TAG = surpriseMe::class.java.simpleName
    }
}
