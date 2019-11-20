package com.example.android.letseat.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;

public class FetchLocation {

    private static final int LOCATION_REQUEST_CODE = 1000;
    private final String LOG_TAG = FetchLocation.class.getSimpleName();
    private WeakReference<Context> weak_context;
    private WeakReference<Activity> weak_activity;

    public FetchLocation(Context context, Activity activity) {
        this.weak_context = new WeakReference<>(context);
        this.weak_activity = new WeakReference<>(activity);
    }

    /**
     * Retrieves Location and stores in myLocation variable
     */
    public Task<Location> getLocation() {

        Context context = weak_context.get();
        Activity activity = weak_activity.get();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Log.d(LOG_TAG, "getLocation: permissions granted");
        }

        FusedLocationProviderClient myFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        Task<Location> task = myFusedLocationClient.getLastLocation();

        return task;

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            // If the permission is granted, get the location,
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Log.d(LOG_TAG, "Failed to get permissions");
            }
        }
    }
}
