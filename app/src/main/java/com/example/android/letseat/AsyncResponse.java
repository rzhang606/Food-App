package com.example.android.letseat;

import org.json.JSONArray;

/**
 * Interface handles communication between FetchDataAsync(aka performing the http request to yelp api) and client
 */
public interface AsyncResponse {
    void processResult(JSONArray output);
}
