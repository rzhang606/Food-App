package com.example.android.letseat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
