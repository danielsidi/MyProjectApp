package com.example.danie.myprojectapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by danie on 04/05/2017.
 */

public class MySqlHelper extends SQLiteOpenHelper {

    Context context;

    public MySqlHelper(Context context) {
        super(context, "location.db " , null , 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQLCreateFavoriteTable="CREATE TABLE "+ DBConstants.tableName+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ DBConstants.NameColumn+" TEXT,  "+DBConstants.AddressColumn+" TEXT,  "+DBConstants.LatColumn+" TEXT , "+DBConstants.LngColumn+" TEXT, "+DBConstants.imageColumn +" TEXT )";
        db.execSQL(SQLCreateFavoriteTable);

        String SQLCreateLastSearchResults="CREATE TABLE "+ DBConstants.searchTableName+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ DBConstants.NameColumn+" TEXT,  "+DBConstants.AddressColumn+" TEXT,  "+DBConstants.LatColumn+" TEXT , "+DBConstants.LngColumn+" TEXT, "+DBConstants.imageColumn +" TEXT )";
        db.execSQL(SQLCreateLastSearchResults);





    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
