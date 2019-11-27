package com.example.android.letseat.utility;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.letseat.App;
import com.example.android.letseat.interfaces.AsyncResponse;
import com.example.android.letseat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Fetches Data from the yelp API based on URL
 */

public class FetchDataAsyncTask extends AsyncTask<URL, Integer, String>{

    private String LOG_TAG = FetchDataAsyncTask.class.getSimpleName();

    /**
     * Implement Interface
     */
    public AsyncResponse delegate;


    @Override
    protected String doInBackground(URL... urls) {

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            for (URL url: urls){
                jsonResponse = makeHttpRequest(url);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return jsonResponse;
    }

    @Override
    protected void onPostExecute(String jsonResponse) {

        //parses the raw string data and populates the sparsearray with business objects

        try{
            JSONArray businessArray = parseJson(jsonResponse);
            if (businessArray == null) {
                Log.e(LOG_TAG, "ERROR: businessArray is null");
            } else {
                delegate.processResult(businessArray);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(LOG_TAG, "ERROR: on post execute: " + e.toString());
        }


    }

    /**
     * Parse Json data into something useable
     */
    private JSONArray parseJson(String rawData) {
        try {
            JSONObject json = new JSONObject(rawData);
            return json.getJSONArray("businesses");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Parse JSON Error");
            e.printStackTrace();
            return null;
        }
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
            urlConnection.setRequestProperty("Authorization", App.getContext().getString(R.string.apikey));
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Log.d(LOG_TAG, "Response: 200 Success");
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e);
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