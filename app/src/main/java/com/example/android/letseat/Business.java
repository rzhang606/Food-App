package com.example.android.letseat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Business implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Business createFromParcel(Parcel in) {
            return new Business(in);
        }

        public Business[] newArray(int size) {
            return new Business[size];
        }
    };

    private String name;
    private boolean isClosed;
    private String imageURL;
    private List<String> categories;
    private int rating;
    private String displayAddress;
    private String phone;
    private double distance;
    private String price;

    public Business(String name, boolean isClosed, String imageURL, List<String> categories, int rating, String displayAddress, String phone, double distance, String price) {
        this.name = name;
        this.isClosed = isClosed;
        this.imageURL = imageURL;
        this.categories = categories;
        this.rating = rating;
        this.displayAddress = displayAddress;
        this.phone = phone;
        this.distance = distance;
        this.price = price;
    }

    public Business(Parcel source) {
        this.name = source.readString();
        this.isClosed = source.readByte() != 0;
        this.imageURL = source.readString();
        categories = new ArrayList<String>();
        source.readList(categories, null);
        this.rating = source.readInt();
        this.displayAddress = source.readString();
        this.phone = source.readString();
        this.distance = source.readDouble();
        this.price = source.readString();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isClosed ? 1 : 0));
        dest.writeString(imageURL);
        dest.writeList(categories);
        dest.writeInt(rating);
        dest.writeString(displayAddress);
        dest.writeString(phone);
        dest.writeDouble(distance);
        dest.writeString(price);
    }

    @Override
    public String toString() {
        String address = displayAddress.replaceAll("[\\p{Punct}&&[^0-9]&&[^,]]", ""); //regex for removing all except comma, numbers
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

    public String getPrice() {
        return price;
    }


}
