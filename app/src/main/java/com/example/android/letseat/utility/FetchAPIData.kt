package com.example.android.letseat.utility

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log

import com.example.android.letseat.interfaces.APIDataResponse
import com.example.android.letseat.App
import com.example.android.letseat.interfaces.AsyncResponse
import com.example.android.letseat.Business
import com.example.android.letseat.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.lang.ref.WeakReference
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

class FetchAPIData(context: Context, activity: Activity) : AsyncResponse {

    private val LOG_TAG = FetchAPIData::class.java.simpleName
    private val fetchDataTask = FetchDataAsyncTask()
    private var query: String? = null

    private val weak_context: WeakReference<Context>
    private val weak_activity: WeakReference<Activity>

    var apiDelegate: APIDataResponse? = null

    init {
        this.weak_context = WeakReference(context)
        this.weak_activity = WeakReference(activity)
        fetchDataTask.delegate = this
    }

    /**
     * Searches the yelp api with the selected string as a parameter
     * First grabs the location to generate the URL, then executes the HTTP request and retrieves the response using the AsyncResponse delegate
     * processResult is called upon retrieving the JSON, and it formats the data
     * then, getData retrieves it to whatever activity called this
     */
    @JvmOverloads
    fun search(searchWord: String, offset: Int = 0) {
        val context = weak_context.get()
        val activity = weak_activity.get()
        query = searchWord

        val location = FetchLocation(context, activity)
        val locationTask = location.location
        locationTask.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val generalURL = URL(App.getContext().getString(R.string.myLocationSearch) +
                            "?latitude=" + location.latitude + "&longitude=" + location.longitude
                            + "&term=" + searchWord + "&offset=" + offset)
                    Log.d(LOG_TAG, "URL created: $generalURL")

                    //get data from yelp api
                    fetchDataTask.execute(generalURL)
                } catch (e: MalformedURLException) {
                    Log.d(LOG_TAG, "URL Malformed")
                }

            } else {
                Log.d(LOG_TAG, "Location finding error: NULL retrieved")
            }
        }
    }

    override fun processResult(output: JSONArray) {
        //receives result of async of onPostExecute
        Log.d(LOG_TAG, "OUTPUT: $output")

        val bArr = ArrayList<Business>()

        try {
            //process JSONArray to usable arraylist
            for (i in 0 until output.length()) {
                val bus = convertBusiness(output.get(i) as JSONObject)
                if (bus != null) {
                    bArr.add(bus)
                }
            }

            Log.d(LOG_TAG, bArr.toString())
            apiDelegate!!.apiResponse(bArr, query)

        } catch (e: JSONException) {
            Log.e(LOG_TAG, "ERROR: JSON Exception")
            e.printStackTrace()
        }

    }

    /**
     * Takes a jsonobject representing a business and creates an instance of our
     * business class, then inserts into the hashmap
     *
     * @param obj   - the json object to be evaluated
     */
    private fun convertBusiness(obj: JSONObject): Business? {
        //get all categories from json
        val categories = ArrayList<String>()
        try {
            //categories
            val jsonCategories = obj.getJSONArray("categories")
            for (i in 0 until jsonCategories.length()) {
                categories.add(jsonCategories.getJSONObject(i).getString("title"))
            }

            //location
            val jsonLocation = obj.getJSONObject("location").getJSONArray("display_address")
            val location = jsonLocation.toString()

            val coordinates = obj.getJSONObject("coordinates")

            //construct new business and put into the map

            return Business(
                    obj.getString("name"),
                    obj.getBoolean("is_closed"),
                    obj.getString("image_url"),
                    categories,
                    obj.getInt("rating"),
                    location,
                    obj.getString("phone"),
                    obj.getDouble("distance"),
                    obj.getString("price"),
                    coordinates.getDouble("latitude"),
                    coordinates.getDouble("longitude")
            )

        } catch (e: JSONException) {
            Log.d(LOG_TAG, "Insert business failed...")
            e.printStackTrace()
        }

        return null

    }
}
