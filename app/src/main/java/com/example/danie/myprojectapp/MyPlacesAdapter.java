package com.example.danie.myprojectapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * Created by danie on 25/04/2017.
 */

public class MyPlacesAdapter extends  RecyclerView.Adapter<MyPlacesAdapter.MyPlacesViewHolder>{

    ArrayList<Place> allPlaces;
    Context context;
    double lat , lng;
    FragmentChanger fragmentChanger;



    public MyPlacesAdapter(ArrayList<Place> allPlaces, Context context, FragmentChanger fragmentChanger, double lat, double lng) {
        this.allPlaces = allPlaces;
        this.context = context;
        this.lat = lat;
        this.lng = lng;
        this.fragmentChanger = fragmentChanger;
    }

    @Override
    public MyPlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate R.layout.single_item to view
        View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent,false);
        //create the view holder with the view
        MyPlacesViewHolder myPlacesViewHolder = new MyPlacesViewHolder(view);
        //return my view holder
        return myPlacesViewHolder;
    }

    @Override
    public void onBindViewHolder(MyPlacesViewHolder holder, int position) {

        Place place = allPlaces.get(position);
       // tay the current place to the item (place);
        holder.bindDataFromAdapterToViews(place);
    }

    @Override
    public int getItemCount() {
        return allPlaces.size();
    }




    public class MyPlacesViewHolder extends RecyclerView.ViewHolder {

        TextView name , address , distance;
        ImageView imageView;
        LinearLayout singleItemLayout;
        Place myItemPlace;


        public MyPlacesViewHolder(View itemView) {

            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameTV);
            address = (TextView) itemView.findViewById(R.id.addressTV);
            distance = (TextView) itemView.findViewById(R.id.distance);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            singleItemLayout = (LinearLayout) itemView.findViewById(R.id.singleItemLayout);



        }
        public void bindDataFromAdapterToViews(final Place currentPlace) {


            name.setText(currentPlace.name);

            if (currentPlace.vicinity == null) {
                address.setText(currentPlace.formatted_address);
            } else if (currentPlace.formatted_address == null) {
                address.setText(currentPlace.vicinity);
            }

            if (currentPlace.photos != null && currentPlace.photos.get(0) != null) {
                Picasso.with(context).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=" + currentPlace.photos.get(0).photo_reference + "&key=AIzaSyCp2ExjzXlQ1CP7W8pGYzRgsV6enzuGyJQ").into(imageView);
            } else {
                imageView.setImageResource(R.drawable.noimage);
            }

            final double dis = DistanceKM(lat, lng, currentPlace.geometry.location.lat, currentPlace.geometry.location.lng);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String KmMiles= preferences.getString("UnitConversionLP",null);
            if (KmMiles == "KM"){
                distance.setText(dis + " KM");
            } else {
                distance.setText(dis/1.61 + "  MILES");
            }


            ///short Click
            singleItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        fragmentChanger.changeFragments(currentPlace);

                }
            });


            ///Long Click
            singleItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, v);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()){
                                case R.id.Add_to_favorite:

                                    //ADD TO FAVORITE

                                    myItemPlace = (Place) allPlaces.get(getAdapterPosition());

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(DBConstants.NameColumn, myItemPlace.name);

                                    if (currentPlace.vicinity == null) {
                                        contentValues.put(DBConstants.AddressColumn, myItemPlace.formatted_address);
                                    } else if (currentPlace.formatted_address == null) {
                                        contentValues.put(DBConstants.AddressColumn, myItemPlace.vicinity);
                                    }

                                    contentValues.put(DBConstants.LatColumn, myItemPlace.geometry.location.lat);
                                    contentValues.put(DBConstants.LngColumn, myItemPlace.geometry.location.lng);
                                    if (myItemPlace.photos != null) {
                                        contentValues.put(DBConstants.imageColumn, myItemPlace.photos.get(0).photo_reference);
                                    }
                                    MySqlHelper mySqlHelper = new MySqlHelper(context);

                                    mySqlHelper.getWritableDatabase().insert(DBConstants.tableName, null, contentValues);

                                    Toasty.success(context, "ADDED", Toast.LENGTH_SHORT, true).show();


                                    break;


                                case R.id.shareIntent:

                                    //share the current location on googleMaps
                                    myItemPlace = (Place) allPlaces.get(getAdapterPosition());
                                    String location="https://www.google.co.il/maps/@"+myItemPlace.geometry.location.lat+","+myItemPlace.geometry.location.lng+",18.79z?hl=en";
                                    //
                                    Intent sharingIntent=new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Place Details");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,location );
                                    context.startActivity(sharingIntent);
                                    break;
                            }

                            return true;
                        }
                    });

                    return true;
                }
            });

        }

    }
    public static double DistanceKM(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }

}
