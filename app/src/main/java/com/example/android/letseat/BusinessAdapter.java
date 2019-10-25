package com.example.android.letseat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class BusinessAdapter extends ArrayAdapter<Business> {

    private int layoutResource;
    private Context mContext;
    private ArrayList<Business> bArray = new ArrayList<Business>();

    /**
     * Adapts the list of business from API request into lisview -> businessListView
     */

    public BusinessAdapter(Context context, int resource, ArrayList<Business> list) {
        super(context, resource, list);
        mContext = context;
        layoutResource = resource;
        bArray = list;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup group){

        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(layoutResource, group, false);
        }

        Business mBusiness = bArray.get(position);

        TextView name = v.findViewById(R.id.row_Name);
        name.setText(mBusiness.getName());

        //TextView distance = v.findViewById(R.id.row_Distance);
        //distance.setText("" + mBusiness.getDistance() + " Meters");


        return v;

    }



}
