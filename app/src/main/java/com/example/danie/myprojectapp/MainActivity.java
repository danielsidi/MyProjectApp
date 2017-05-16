package com.example.danie.myprojectapp;

import
        android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements FragmentChanger {

    ListFragment listFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


if (getFragmentManager().findFragmentByTag("frag") == null) {

    listFragment = new ListFragment();
    getFragmentManager().beginTransaction().replace(R.id.MainActivityID, listFragment, "frag").commit();
        }

    }





    ////////// MENU //////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        /////////  click on settings_item open my settings class  /////////
        if (item.getItemId() == R.id.settings_item) {
            Intent intent = new Intent(MainActivity.this, MySettingsAct.class);
            startActivity(intent);



        /////////  click on exit_item  /////////
        }else if (item.getItemId() == R.id.exit_item) {

            //the dialog builder:
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            //create the dialog:
            AlertDialog dialog = builder
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
//                        .setIcon(R.drawable.ic_launcher)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                            Toasty.info(MainActivity.this, "see you soon!", Toast.LENGTH_SHORT, true).show();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    })
                    .create();
            //show the dialog:
            dialog.show();
        }else if (item.getItemId() == R.id.favorites) {

            Intent intent = new Intent(MainActivity.this , FavoriteAct.class);
            startActivity(intent);

        }
        return true;
    }

    ////changeFragments////
    @Override
    public void changeFragments(final Place place) {
        MapFragment mapFragment = new MapFragment();

        if (isLargeDevice()) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.addToBackStack("change map");
            transaction.replace(R.id.rightCotainer, mapFragment).commit();

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    LatLng latLng = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    googleMap.moveCamera(update);
                }
            });


        } else {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.addToBackStack("change map");
            transaction.replace(R.id.MainActivityID, mapFragment).commit();

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    LatLng latLng = new LatLng(place.geometry.location.lat, place.geometry.location.lng);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    googleMap.moveCamera(update);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>0)
        {
            getFragmentManager().popBackStack();
        }
        else
        {
            super.onBackPressed();
        }
      //  Log.d("hh","kk");
      //  getFragmentManager()
      //  super.onBackPressed();

    }




    private boolean isLargeDevice()
    {
        boolean isLarge=false;
        LinearLayout rightLayout=(LinearLayout) findViewById(R.id.rightCotainer);
        if(rightLayout != null)
        {
            isLarge=true;
        }
        return isLarge;
    }


    public static class myReceiverBattery extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED"))
            Toasty.info(context , "Power Connected", Toast.LENGTH_SHORT, true).show();
            else{
                Toasty.info(context , "Power Disconnected", Toast.LENGTH_SHORT, true).show();
            }
        }
    }



}
