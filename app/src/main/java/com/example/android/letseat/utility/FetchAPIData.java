package com.example.android.letseat.utility;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.android.letseat.App;
import com.example.android.letseat.AsyncResponse;
import com.example.android.letseat.Business;
import com.example.android.letseat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchAPIData implements AsyncResponse {

    private final String LOG_TAG = FetchAPIData.class.getSimpleName();
    private FetchDataAsyncTask fetchDataTask = new FetchDataAsyncTask();
    private ArrayList<Business> bArray = new ArrayList<>();

    private Context context;
    private Activity activity;

    public FetchAPIData(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        fetchDataTask.delegate = this;
    }

    public ArrayList<Business> getData() {
        return bArray;
    }

    /**
     * Searches the yelp api with the selected string as a parameter
     * First grabs the location to generate the URL, then executes the HTTP request and retrieves the response using the AsyncResponse delegate
     * processResult is called upon retrieving the JSON, and it formats the data
     * then, getData retrieves it to whatever activity called this
     */
    public void search(String searchWord) {
        FetchLocation location = new FetchLocation(context, activity);
        Task<Location> locationTask = location.getLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    try {
                        URL generalURL = new URL(App.getContext().getString(R.string.myLocationSearch) + "?latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude());
                        Log.d(LOG_TAG, "URL created: " + generalURL.toString());

                        //get data from yelp api
                        fetchDataTask.execute(generalURL);
                    } catch (MalformedURLException e) {
                        Log.d(LOG_TAG, "URL Malformed");
                    }

                } else {
                    Log.d(LOG_TAG, "Location finding error: NULL retrieved");
                }
            }
        });
    }

    public void processResult(JSONArray output) {
        //receives result of async of onPostExecute
        Log.d(LOG_TAG, "OUTPUT: " + output.toString());

        try{
            //process JSONArray to usable arraylist
            for(int i = 0; i < output.length(); i++){
                bArray.add(insertBusiness((JSONObject)output.get(i)));
            }

            Log.d(LOG_TAG, bArray.toString());

        } catch (JSONException e) {
            Log.e(LOG_TAG, "ERROR: JSON Exception");
            e.printStackTrace();
        }
    }

    /**
     * Takes a jsonobject representing a business and creates an instance of our
     * business class, then inserts into the hashmap
     *
     * @param obj   - the json object to be evaluated
     */
    private Business insertBusiness(JSONObject obj) {
        //get all categories from json
        List<String> categories = new ArrayList<>();
        try {
            //categories
            JSONArray jsonCategories = obj.getJSONArray("categories");
            for (int i = 0; i < jsonCategories.length(); i++) {
                categories.add(jsonCategories.getJSONObject(i).getString("title"));
            }

            //location
            JSONArray jsonLocation = obj.getJSONObject("location").getJSONArray("display_address");
            String location = jsonLocation.toString();
            //Log.d(LOG_TAG, "NAME OF LOCATION: " + location);

            //construct new business and put into the map
            Business business = new Business(
                    obj.getString("name"),
                    obj.getBoolean("is_closed"),
                    obj.getString("image_url"),
                    categories,
                    obj.getInt("rating"),
                    location,
                    obj.getString("phone"),
                    obj.getDouble("distance"),
                    obj.getString("price")
            );

            return business;

        } catch (JSONException e) {
            Log.d(LOG_TAG, "Insert business failed...");
            e.printStackTrace();
        }

        return null;

    }

}
