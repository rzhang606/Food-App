package com.example.android.letseat.utility

import android.os.AsyncTask
import android.util.Log

import com.example.android.letseat.App
import com.example.android.letseat.interfaces.AsyncResponse
import com.example.android.letseat.R

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * Fetches Data from the yelp API based on URL
 */

class FetchDataAsyncTask : AsyncTask<URL, Int, String>() {

    private val LOG_TAG = FetchDataAsyncTask::class.java.simpleName

    /**
     * Implement Interface
     */
    var delegate: AsyncResponse? = null


    override fun doInBackground(vararg urls: URL): String {

        // Perform HTTP request to the URL and receive a JSON response back
        var jsonResponse = ""
        try {
            for (url in urls) {
                jsonResponse = makeHttpRequest(url)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e)
        }

        return jsonResponse
    }

    override fun onPostExecute(jsonResponse: String) {

        //parses the raw string data and populates the sparsearray with business objects

        try {
            val businessArray = parseJson(jsonResponse)
            if (businessArray == null) {
                Log.e(LOG_TAG, "ERROR: businessArray is null")
            } else {
                delegate!!.processResult(businessArray)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, "ERROR: on post execute: $e")
        }


    }

    /**
     * Parse Json data into something useable
     */
    private fun parseJson(rawData: String): JSONArray? {
        try {
            val json = JSONObject(rawData)
            return json.getJSONArray("businesses")

        } catch (e: JSONException) {
            Log.e(LOG_TAG, "Parse JSON Error")
            e.printStackTrace()
            return null
        }

    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    @Throws(IOException::class)
    private fun makeHttpRequest(url: URL?): String {
        var jsonResponse = ""

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse
        }

        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.setRequestProperty("Authorization", App.getContext().getString(R.string.apikey))
            urlConnection.connect()

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.responseCode == 200) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
                Log.d(LOG_TAG, "Response: 200 Success")
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.responseCode)
            }

        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e)
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    /**
     * Convert the [InputStream] into a String which contains the
     * whole JSON response from the server.
     */
    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line: String? = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }
}