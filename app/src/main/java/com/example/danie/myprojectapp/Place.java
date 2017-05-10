package com.example.danie.myprojectapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by danie on 25/04/2017.
 */

public class Place implements Parcelable {
    String name;
    String formatted_address;
    String vicinity;
    geometry geometry;
    ArrayList <MyPhoto> photos;
    double lat;
    double lng;
    String photo;

    protected Place(Parcel in) {
        name = in.readString();
        formatted_address = in.readString();
        vicinity = in.readString();
    }

    public Place(String name, String formatted_address, double lat, double lng, String photo, String vicinity) {
        this.name = name;
        this.formatted_address = formatted_address;
        this.lat = lat;
        this.lng = lng;
        this.photo = photo;
        this.vicinity = vicinity;
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(formatted_address);
        dest.writeString(vicinity);
    }
}
