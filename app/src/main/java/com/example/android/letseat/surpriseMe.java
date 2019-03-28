package com.example.android.letseat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class surpriseMe extends AppCompatActivity {

    private static final String LOG_TAG = surpriseMe.class.getSimpleName();
    private static final int LOCATION_REQUEST_CODE = 1000;


    private FusedLocationProviderClient mFusedLocationClient;
    private Location myLocation;

    SparseArray<Business> bArray = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise_me);

        getLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                // If the permission is granted, get the location,
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Log.d(LOG_TAG, "Failed to get permissions");
                }
                break;
        }
    }

    /**
     * Retrieves Location and stores in myLocation variable
     * Once location is retrieved, fires off the Async Task for API call
     */
    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Log.d(LOG_TAG, "getLocation: permissions granted");
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = mFusedLocationClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                    Log.d(LOG_TAG, "My location is " + myLocation.toString());
                } else {
                    Log.d(LOG_TAG, "Location finding error: NULL retrieved");
                }
            }
        });

        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //get data from yelp api
                FetchDataAsyncTask fetchDataTask = new FetchDataAsyncTask();
                fetchDataTask.execute();
            }
        });

    }



    public class FetchDataAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... url) {

            URL generalURL = null;

            //Create URL object
            try {
                generalURL = createUrl(getString(R.string.myLocationSearch) + "?latitude=" + myLocation.getLatitude() + "&longitude=" + myLocation.getLongitude());
                Log.d(LOG_TAG, "URL created: " + generalURL.toString());
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "mylocation is null");
            }


            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(generalURL);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {

            parseJson(jsonResponse);

            Random rand = new Random();
            int myRand = rand.nextInt(20); //between 0-19

            TextView display = findViewById(R.id.resultsText);
            Log.d(LOG_TAG, "My Random Number: " + myRand);
            display.setText(bArray.get(myRand).toString());
        }

        /**
         * Parse Json data into something useable
         */
        private void parseJson(String rawData) {
            try {

                JSONObject json = new JSONObject(rawData);
                JSONArray businessArray = json.getJSONArray("businesses");

                //turn jsonarray which represents businesses list into map of Business class
                for (int i = 0; i < businessArray.length(); i++)
                    insertBusiness((JSONObject) businessArray.get(i), i);

            } catch (JSONException e) {
                Log.d(LOG_TAG, "Parse JSON Error");
                e.printStackTrace();
            }

        }

        /**
         * Takes a jsonobject representing a business and creates an instance of our
         * business class, then inserts into the hashmap
         *
         * @param obj   - the json object to be evaluated
         * @param index - index of the object on the hashmap
         */
        private void insertBusiness(JSONObject obj, int index) {
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
                Log.d(LOG_TAG, "NAME OF LOCATION: " + location);


                //construct new business and put into the map
                Business business = new Business(
                        obj.getString("name"),
                        obj.getBoolean("is_closed"),
                        obj.getString("image_url"),
                        categories,
                        obj.getInt("rating"),
                        location,
                        obj.getString("phone"),
                        obj.getDouble("distance")
                );
                bArray.append(index, business);
                Log.d(LOG_TAG, "Businesses Count: " + bArray.size());
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Insert business failed...");
                e.printStackTrace();
            }

        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL: " + stringUrl, exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            // If the URL is null, then return early.
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestProperty("Authorization", getString(R.string.apikey));
                urlConnection.connect();

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }


}
