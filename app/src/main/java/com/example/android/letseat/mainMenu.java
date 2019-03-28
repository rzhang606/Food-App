package com.example.android.letseat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class mainMenu extends AppCompatActivity {

    private static final String LOG_TAG = mainMenu.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

    }

    /**
     * Launches the SurpriseMe activity
     *
     * @param view
     */
    public void surpriseMeButton(View view) {
        Log.d(LOG_TAG, "Surprise Me clicked");
        Intent intent = new Intent(this, surpriseMe.class);
        startActivity(intent);
    }

    /**
     * Search for food manually
     *
     * @param view
     */
    public void searchButton(View view) {

    }


}
