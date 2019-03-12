package com.example.android.letseat;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

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

    LocationManager currentLocation;

    private static final String LOG_TAG = surpriseMe.class.getSimpleName();

    HashMap<Integer, Business> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surprise_me);
        currentLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //get data from yelp api
        FetchDataAsyncTask task = new FetchDataAsyncTask();
        task.execute();
    }


    public class FetchDataAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... url) {

            //Create URL object
            URL generalURL = createUrl(getString(R.string.businessSearchURL));

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
            display.setText(map.get(myRand).toString());
        }

        /**
         * Parse Json data into something useable
         */
        private String parseJson(String rawdata) {
            try {

                JSONObject json = new JSONObject(rawdata);
                JSONArray businessArray = json.getJSONArray("businesses");

                //turn jsonarray which represents businesses list into map of Business class
                for (int i = 0; i < businessArray.length(); i++) {
                    insertBusiness((JSONObject) businessArray.get(i), i);
                }

            } catch (JSONException e) {
                Log.d(LOG_TAG, "Parse JSON Error");
                e.printStackTrace();
            }

            return "";
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
                map.put(index, business);
                Log.d(LOG_TAG, "Businesses Count: " + map.size());
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
                Log.e(LOG_TAG, "Error with creating URL", exception);
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
