package com.example.android.letseat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class BusinessListView extends AppCompatActivity {

    private final String LOG_TAG = BusinessListView.class.getSimpleName();

    ArrayList<Business> bArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list_view);

        Intent intent = getIntent();
        bArray = intent.getParcelableArrayListExtra("DATA");

        ListView myListView = findViewById(R.id.myListView);

        ArrayList<String> businessNames = new ArrayList<String>();

        for (int i = 0; i < bArray.size(); i++) {
            businessNames.add(bArray.get(i).getName());
        }

        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, businessNames);

        BusinessAdapter arrayAdapter = new BusinessAdapter(this, R.layout.business_row, bArray);

        myListView.setAdapter(arrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


    }

}
