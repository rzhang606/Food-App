package com.example.android.letseat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessDisplayFragment extends Fragment {


    public BusinessDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_display, container, false);

    }

    /**
     * Populates the view with properties from the business class
     */

    public void Initialize(Business myBusiness) {
        View view = getView();

        if (view != null) {

            TextView name = (TextView) view.findViewById(R.id.b_frag_Name);
            name.setText(myBusiness.getName());

            TextView category = (TextView) view.findViewById(R.id.b_frag_Category);
            category.setText(myBusiness.getCategories().toString());

            TextView rating = (TextView) view.findViewById(R.id.b_frag_Rating);
            rating.setText("(" + myBusiness.getRating() + "/5)");

            TextView distance = (TextView) view.findViewById(R.id.b_frag_Distance);
            DecimalFormat dec = new DecimalFormat("#0.00");
            distance.setText(dec.format(myBusiness.getDistance()) + " meters");
        }

    }



}
