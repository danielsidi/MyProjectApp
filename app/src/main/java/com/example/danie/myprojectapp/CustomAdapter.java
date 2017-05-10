package com.example.danie.myprojectapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by danie on 04/05/2017.
 */

public class CustomAdapter extends CursorAdapter  {
    public CustomAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.favorite_single_item,null);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String theNameOfThePlace= cursor.getString(cursor.getColumnIndex(DBConstants.NameColumn));
        TextView itemNameTV = (TextView)view.findViewById(R.id.nameTV);
        itemNameTV.setText(theNameOfThePlace);

        String theAddressOfThePlace = cursor.getString(cursor.getColumnIndex(DBConstants.AddressColumn));
        TextView addressTV = (TextView) view.findViewById(R.id.addressTV);
        addressTV.setText(theAddressOfThePlace);

        //todo: image

    }
}
