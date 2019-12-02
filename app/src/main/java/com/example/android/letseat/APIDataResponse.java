package com.example.android.letseat;

import java.util.ArrayList;

/**
 * Interface handles communication between FetchAPIData utility and client
 */
public interface APIDataResponse {
    void apiResponse(ArrayList<Business> bArr, String query);
}
