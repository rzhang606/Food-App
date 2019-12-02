package com.example.android.letseat.interfaces;

import com.example.android.letseat.Business;

import java.util.ArrayList;

/**
 * Interface handles communication between FetchAPIData utility and client
 */
public interface APIDataResponse {
    void apiResponse(ArrayList<Business> bArr, String query);
}
