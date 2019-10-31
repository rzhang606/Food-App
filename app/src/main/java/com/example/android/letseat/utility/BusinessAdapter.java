package com.example.android.letseat.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.letseat.Business;
import com.example.android.letseat.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BusinessAdapter extends ArrayAdapter<Business> {

    private int layoutResource;
    private Context mContext;
    private ArrayList<Business> bArray = new ArrayList<Business>();
    private HashMap<String, Integer> category_to_icon = new HashMap<>();

    /**
     * Adapts the list of business from API request into lisview -> businessListView
     */

    public BusinessAdapter(Context context, int resource, ArrayList<Business> list) {
        super(context, resource, list);
        mContext = context;
        layoutResource = resource;
        bArray = list;

        category_to_icon.put("Burgers", R.drawable.burger);
        category_to_icon.put("Beer", R.drawable.beer);
        category_to_icon.put("Breweries", R.drawable.beer);
        category_to_icon.put("Bakery", R.drawable.cookie);
        category_to_icon.put("Desserts", R.drawable.cake);
        category_to_icon.put("Breakfast & Brunch", R.drawable.breakfast);
        category_to_icon.put("Candy", R.drawable.candy);
        category_to_icon.put("Coffee & Tea", R.drawable.coffee);
        category_to_icon.put("Coffee Roasteries", R.drawable.coffee);
        category_to_icon.put("Steak House", R.drawable.meat);
        category_to_icon.put("Pizza", R.drawable.pizza);
        category_to_icon.put("Pasta", R.drawable.spaguetti);
        category_to_icon.put("Japanese", R.drawable.sushi);
        category_to_icon.put("Tea", R.drawable.tea);
        category_to_icon.put("Fruit", R.drawable.watermelon);
        category_to_icon.put("Bubble Tea", R.drawable.boba);
        category_to_icon.put("Seafood", R.drawable.crab);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup group){

        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(layoutResource, group, false);
        }

        Business mBusiness = bArray.get(position);

        ImageView icon = v.findViewById(R.id.row_Icon);
        int icon_image_id = R.drawable.cookie;
        for(String category: mBusiness.getCategories()) {
            if(category_to_icon.get(category) != null) {
                icon_image_id = category_to_icon.get(category);
                break;
            }
        }
        icon.setImageResource(icon_image_id);

        TextView name = v.findViewById(R.id.row_Name);
        name.setText(mBusiness.getName());

        //TextView distance = v.findViewById(R.id.row_Distance);
        //distance.setText("" + mBusiness.getDistance() + " Meters");


        return v;

    }



}
