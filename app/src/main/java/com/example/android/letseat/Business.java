package com.example.android.letseat;

import android.support.annotation.NonNull;

import java.util.List;

public class Business {
    private String name;
    private boolean isClosed;
    private String imageURL;
    private List<String> categories;
    private int rating;
    private String displayAddress;
    private String phone;
    private double distance;

    Business(String name, boolean isClosed, String imageURL, List<String> categories, int rating, String displayAddress, String phone, double distance) {
        this.name = name;
        this.isClosed = isClosed;
        this.imageURL = imageURL;
        this.categories = categories;
        this.rating = rating;
        this.displayAddress = displayAddress;
        this.phone = phone;
        this.distance = distance;
    }

    @Override
    public String toString() {
        String address = displayAddress.replaceAll("[\\p{Punct}&&[^0-9]&&[^,]]", ""); //regex for removing all except comma,num
        return name + " (" + rating + "/5) Rating On Yelp, " + System.getProperty("line.separator") + address
                + ", " + phone;
    }

    public String getName() {
        return name;
    }

    public boolean getIsClosed() {
        return isClosed;
    }

    public String getImageURL() {
        return imageURL;
    }

    public List<String> getCategories() {
        return categories;
    }

    public int getRating() {
        return rating;
    }

    public String getDisplayAddress() {
        return displayAddress;
    }

    public String getPhone() {
        return phone;
    }

    public double getDistance() {
        return distance;
    }

}
