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

public class surpriseMe extends AppCompatActivity {

    LocationManager currentLocation;

    private static final String LOG_TAG = mainMenu.class.getSimpleName();

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
            TextView results = findViewById(R.id.resultsText);
            results.setText(jsonResponse);
            parseJson(jsonResponse);
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
                    JSONObject obj = (JSONObject) businessArray.get(i);

                    //get all categories from json
                    List<String> categories = new ArrayList<>();
                    categories.add(obj.getJSONObject("categories").getString("title"));

                    //construct new business and put into the map
                    Business business = new Business(
                            obj.getString("name"),
                            obj.getBoolean("is_closed"),
                            obj.getString("image_url"),
                            categories,
                            obj.getInt("rating"),
                            obj.getString("display_address"),
                            obj.getString("phone"),
                            obj.getDouble("distance")
                    );
                    map.put(i, business);
                }


            } catch (JSONException e) {
                Log.d(LOG_TAG, "Parse JSON Error");
                e.printStackTrace();
            }

            return "";
        }


        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
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
