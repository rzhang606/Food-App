package com.example.android.letseat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class search extends AppCompatActivity {

    /**
     * This activity should let the users more specifically choose what they want
     * - provide filters to search for certain categories of food,
     * - allow certain types of food to not appear (ie, no mexican food)
     * - search for restaurant names
     * -
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


    }

}
