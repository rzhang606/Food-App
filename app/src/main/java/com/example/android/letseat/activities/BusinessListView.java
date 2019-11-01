package com.example.android.letseat.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.letseat.Business;
import com.example.android.letseat.utility.BusinessAdapter;
import com.example.android.letseat.R;

import java.util.ArrayList;

public class BusinessListView extends AppCompatActivity {

    private final String LOG_TAG = BusinessListView.class.getSimpleName();

    private ArrayList<Business> bArray = new ArrayList<>();

    /**
     * Fetches data from source (search.java) and instantiates the adapter for the listview
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list_view);

        Intent intent = getIntent();
        bArray = intent.getParcelableArrayListExtra("DATA");

        ListView myListView = findViewById(R.id.myListView);

        BusinessAdapter arrayAdapter = new BusinessAdapter(this, R.layout.business_row, bArray);

        myListView.setAdapter(arrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("BUSINESS", bArray.get(position));

                Intent intent = new Intent(BusinessListView.this, surpriseMe.class);
                intent.putExtra("Bundle", bundle);
                intent.putExtra("FROM_MAIN", 0);

                startActivity(intent);

            }
        });


    }

}
