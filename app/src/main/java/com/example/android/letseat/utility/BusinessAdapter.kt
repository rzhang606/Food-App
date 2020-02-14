package com.example.android.letseat.utility

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.example.android.letseat.Business
import com.example.android.letseat.R

import java.util.ArrayList
import java.util.HashMap

class BusinessAdapter
/**
 * Adapts the list of business from API request into lisview -> businessListView
 */
(private val mContext: Context, private val layoutResource: Int, list: ArrayList<Business>) : ArrayAdapter<Business>(mContext, layoutResource, list) {
    private var bArray = ArrayList<Business>()
    private val category_to_icon = HashMap<String, Int>()

    init {
        bArray = list

        category_to_icon["Burgers"] = R.drawable.burger

        category_to_icon["Beer"] = R.drawable.beer
        category_to_icon["Breweries"] = R.drawable.beer
        category_to_icon["Bars"] = R.drawable.beer
        category_to_icon["Pubs"] = R.drawable.beer

        category_to_icon["Bakery"] = R.drawable.cookie
        category_to_icon["Bakeries"] = R.drawable.cookie

        category_to_icon["Desserts"] = R.drawable.cake

        category_to_icon["Breakfast & Brunch"] = R.drawable.breakfast

        category_to_icon["Candy"] = R.drawable.candy

        category_to_icon["Coffee & Tea"] = R.drawable.coffee
        category_to_icon["Coffee Roasteries"] = R.drawable.coffee

        category_to_icon["Steak House"] = R.drawable.meat

        category_to_icon["Noodles"] = R.drawable.noodles

        category_to_icon["Salad"] = R.drawable.salad

        category_to_icon["Pizza"] = R.drawable.pizza

        category_to_icon["Pasta"] = R.drawable.spaguetti

        category_to_icon["Japanese"] = R.drawable.sushi
        category_to_icon["Sushi Bars"] = R.drawable.sushi

        category_to_icon["Tea"] = R.drawable.tea
        category_to_icon["Tea Rooms"] = R.drawable.tea
        category_to_icon["Bubble Tea"] = R.drawable.boba


        category_to_icon["Fruit"] = R.drawable.watermelon

        category_to_icon["Seafood"] = R.drawable.crab

        category_to_icon["Sandwiches"] = R.drawable.sandwich

        category_to_icon["Soup"] = R.drawable.soup

        category_to_icon["Middle Eastern"] = R.drawable.kebab
        category_to_icon["Falafel"] = R.drawable.kebab
        category_to_icon["Lebanese"] = R.drawable.kebab

        category_to_icon["Bagel"] = R.drawable.bagel

    }

    override fun getView(position: Int, convertView: View?, group: ViewGroup): View {

        var v = convertView

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(layoutResource, group, false)
        }

        val mBusiness = bArray[position]

        val icon = v!!.findViewById<ImageView>(R.id.row_Icon)
        var icon_image_id = R.drawable.mdefault
        for (category in mBusiness.categories) {
            if (category_to_icon[category] != null) {
                icon_image_id = category_to_icon[category]!!
                break
            }
        }
        icon.setImageResource(icon_image_id)

        val name = v.findViewById<TextView>(R.id.row_Name)
        name.text = mBusiness.name

        val price = v.findViewById<TextView>(R.id.row_Price)
        price.text = mBusiness.price

        val distance = v.findViewById<TextView>(R.id.row_Distance)
        val mdistance = java.lang.Math.floor(mBusiness.distance - mBusiness.distance % 100).toInt()
        if (mdistance < 100) {
            distance.text = " <100 M"
        } else {
            distance.text = "$mdistance M"
        }


        return v

    }


}
