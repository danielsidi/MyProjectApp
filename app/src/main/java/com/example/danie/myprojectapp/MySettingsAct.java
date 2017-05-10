package com.example.danie.myprojectapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.util.List;

import es.dmoral.toasty.Toasty;


/**
 * Created by danie on 21/04/2017.
 */

public class MySettingsAct extends PreferenceActivity {

    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //add Preferences From Resource
        addPreferencesFromResource(R.xml.prefs);

        //TODO: STRINGS.XML to all

        ///////////////////      listPreference        /////////////////////////
        ListPreference listPreference = (ListPreference) findPreference("UnitConversionLP");

        String[] allOptions= new String[]{ "KM", "MILES" };
        String[] allOptionsValues= new String[]{ "KM", "MILES" };

        listPreference.setEntries(allOptions);
        listPreference.setEntryValues(allOptionsValues);

        //On Preference Change Listener
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Toasty.info(MySettingsAct.this , "changed to "+newValue, Toast.LENGTH_SHORT).show();

                ((ListPreference)preference).setTitle("Choose Unit: "+newValue);

                return true;
            }
        });


        ///////////////////      DeleteFavoritesP        /////////////////////////
        Preference DeleteFavoritesP= (Preference) findPreference("DeleteFavoritesP");
        DeleteFavoritesP.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                //alert dialog
                //delete favorites

                //the dialog builder:
                AlertDialog.Builder builder = new AlertDialog.Builder(MySettingsAct.this);

                //create the dialog:
                AlertDialog dialog = builder
                        .setTitle("Delete Favorites")
                        .setMessage("Are you sure you want to delete favorites?")
//                        .setIcon(R.drawable.ic_launcher)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                MySqlHelper mySqlHelper = new MySqlHelper(MySettingsAct.this);
                                mySqlHelper.getWritableDatabase().delete(DBConstants.tableName , null , null);

                                Toasty.success(MySettingsAct.this, "Favorites deleted!", Toast.LENGTH_SHORT, true).show();

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


                return true;
            }
        });

        ///////////////////      ExitSettingsP        /////////////////////////
        Preference exitP= (Preference) findPreference("ExitSettingsP");
        exitP.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                finish();

                return true;
            }
        });




    }
}
