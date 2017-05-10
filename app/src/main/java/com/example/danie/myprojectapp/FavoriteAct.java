package com.example.danie.myprojectapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class FavoriteAct extends AppCompatActivity {
    ListView myLV;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

//
        MySqlHelper mySqlHelper = new MySqlHelper(this);

        Cursor cursor = mySqlHelper.getReadableDatabase().query(DBConstants.tableName, null , null , null , null , null , null);

        adapter = new CustomAdapter(this,cursor);
        myLV = (ListView)findViewById(R.id.myLV);
        myLV.setAdapter(adapter);




    }
}
