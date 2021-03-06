package com.example.danie.myprojectapp;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;


import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyIntentService extends IntentService {


    public MyIntentService() {
        super("MyIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String searchWord = intent.getStringExtra("searchWord");
        double lat = intent.getDoubleExtra("lat" , 0.0);
        double lng = intent.getDoubleExtra("lng" , 0.0);
        String action = intent.getAction();


        String url="";
        if (action == "isNotChecked")
            url="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+searchWord+"&key=AIzaSyAdl51G7iH0wF46jQGLsMoNXQeuYl6l5_A";
        else if (action == "isChecked")
            url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=2000&keyword="+searchWord+"&key=AIzaSyAdl51G7iH0wF46jQGLsMoNXQeuYl6l5_A";

        DownlaodJson downlaodJson = new DownlaodJson();
        String response = null;
        try {
            response = downlaodJson.run(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        GsonModel gsonMainObject = gson.fromJson(response , GsonModel.class );

        ArrayList<Place>allplaces=gsonMainObject.results;
        Log.d ("Dsdfsf",  ""+allplaces.size());

        ContentValues contentValues = new ContentValues();
        MySqlHelper mySqlHelper = new MySqlHelper(this);
        mySqlHelper.getWritableDatabase().delete(DBConstants.searchTableName, null , null);
        for (int i = 0; i < allplaces.size() ; i++) {
            contentValues.put(DBConstants.NameColumn , allplaces.get(i).name);
            if (allplaces.get(i).vicinity == null) {
                contentValues.put(DBConstants.AddressColumn , allplaces.get(i).formatted_address);
            } else if (allplaces.get(i).formatted_address == null) {
                contentValues.put(DBConstants.AddressColumn , allplaces.get(i).vicinity);
            }
            contentValues.put(DBConstants.LatColumn, allplaces.get(i).geometry.location.lat);
            contentValues.put(DBConstants.LngColumn, allplaces.get(i).geometry.location.lng);
            if (allplaces.get(i).photos != null) {
                contentValues.put(DBConstants.imageColumn, allplaces.get(i).photos.get(0).photo_reference);
            }

            mySqlHelper.getWritableDatabase().insert(DBConstants.searchTableName, null, contentValues);




        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString("lastSearch", searchWord);
        preferences.edit().putString("action" , action);



        Intent sendToBroadCastIntent = new Intent("finished");
        sendToBroadCastIntent.putParcelableArrayListExtra("allPlacesFromService" , allplaces);
        sendToBroadCastIntent.putExtra("lat",lat);
        sendToBroadCastIntent.putExtra("lng",lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendToBroadCastIntent);
        }
    }

