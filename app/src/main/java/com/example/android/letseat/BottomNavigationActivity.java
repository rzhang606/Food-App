package com.example.android.letseat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.letseat.activities.MapsActivity;
import com.example.android.letseat.activities.search;
import com.example.android.letseat.activities.surpriseMe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationActivity extends AppCompatActivity {
    /**
     * Parent activity for all activities with the bottom nav bar
     */

    BottomNavigationView bottomNavigationView;
    Context mcontext;

   @Override
    protected void onCreate(Bundle savedInstanceState ) {
       super.onCreate(savedInstanceState);
       mcontext = this;
   }

   protected void setNavigationListener() {
       bottomNavigationView = findViewById(R.id.bottom_navigation);
       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               Intent intent;
               switch(menuItem.getItemId()) {
                   case R.id.surprise_option:
                       intent = new Intent(mcontext, surpriseMe.class);
                       startActivity(intent);
                       break;
                   case R.id.search_option:
                       intent = new Intent(mcontext, search.class);
                       startActivity(intent);
                       break;
                   case R.id.map_option:
                       intent = new Intent(mcontext, MapsActivity.class);
                       startActivity(intent);
                       break;
               }
               return true;
           }
       });
   }


}
