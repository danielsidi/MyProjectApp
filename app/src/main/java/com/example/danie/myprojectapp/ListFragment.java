package com.example.danie.myprojectapp;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.jar.Manifest;

import es.dmoral.toasty.Toasty;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements LocationListener {


    EditText searchET;
    CheckBox nerbyCB;
    RecyclerView myRV;
    String searchWord;
    Double lat, lng;
    LocationManager locationManager;
    Location currentLocation;
    int permissionCheck;
    ArrayList <Place> places;
    MyPlacesAdapter myPlacesAdapter;
    Bundle IsSavedInstance;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_list, container, false);
        IsSavedInstance = savedInstanceState;
        // find view by id
        searchET = (EditText) view.findViewById(R.id.searchET);
        nerbyCB = (CheckBox) view.findViewById(R.id.checkBox);
        myRV = (RecyclerView) view.findViewById(R.id.myRV);





        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,1 , this);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 8);
        }



        //ok button
        view.findViewById(R.id.OkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check Connection
                CheckConnection checkConnection = new CheckConnection(getActivity());
                //if is network available , start intent from listfragment to MyIntentService
                if (checkConnection.isNetworkAvailable()) {

                    Toasty.success(getActivity(), "Connected", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MyIntentService.class);

                    // check if checkbox is checked
                    //if is checked set action : isChecked
                    //else set action : isNotChecked
                    if (nerbyCB.isChecked()) {

                        try {
                            searchWord = URLEncoder.encode(searchET.getText().toString(), "UTF-8");

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("searchWord", searchWord);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        intent.setAction("isChecked");
                    } else { //if is Not Checked

                        try {
                            searchWord = URLEncoder.encode(searchET.getText().toString(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("searchWord", searchWord);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        intent.setAction("isNotChecked");
                    }
                    //start service intent
                    getActivity().startService(intent);
                    //network not Connected
                } else {
                    Toasty.error(getActivity(), "Not Connected!", Toast.LENGTH_SHORT).show();
                }

            }

        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new myReciver(), new IntentFilter("finished"));



        if(savedInstanceState != null)// means that screen is after rotation
        {

            myRV.setLayoutManager(new LinearLayoutManager(getActivity()));
            ArrayList<Place> landscape = new ArrayList<>();
            landscape=savedInstanceState.getParcelableArrayList("places");
            lat = savedInstanceState.getDouble("lat");
            lng = savedInstanceState.getDouble("lng");

            places=landscape;
            myPlacesAdapter = new MyPlacesAdapter(places, getActivity(), (FragmentChanger) getActivity(), lat, lng);
            myRV.setAdapter(myPlacesAdapter);

        }



        return view;
    }






    @Override
    public void onLocationChanged(Location location) {

            lat = location.getLatitude();
            lng = location.getLongitude();
        }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toasty.info(getActivity(), "provider enabled: "+provider, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toasty.info(getActivity(), "provider disabled: "+provider, Toast.LENGTH_SHORT).show();
    }

    public  void startGps (){

        if (ActivityCompat.checkSelfPermission(getActivity() , android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( getActivity() , android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,1 , this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 8){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startGps();
            }else{
                Toasty.info(getActivity(), "You must open gps", Toast.LENGTH_SHORT).show();
            }

        }





    }


    //BroadcastReceiver
    class myReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
             places = intent.getParcelableArrayListExtra("allPlacesFromService");
            lat = intent.getDoubleExtra("lat" , 0.0);
            lng = intent.getDoubleExtra("lng", 0.0);

            refresfList();



        }
    }

    private void refresfList() {

        myRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        myPlacesAdapter = new MyPlacesAdapter(places, getActivity(),(FragmentChanger)getActivity(), lat, lng);
        myRV.setAdapter(myPlacesAdapter);


    }


    @Override
    public void onResume() {
        super.onResume();
        MySqlHelper mySqlHelper = new MySqlHelper(getActivity());
        Cursor cursor = mySqlHelper.getReadableDatabase().query(DBConstants.searchTableName, null, null, null, null, null, null);
        if (IsSavedInstance == null) {


            if (places != null) {
                refresfList();
            }else if (cursor.getCount()!=0){
                places=new ArrayList<>();

                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(DBConstants.NameColumn));
                    String formatted_address = cursor.getString(cursor.getColumnIndex(DBConstants.AddressColumn));
                    String vicinity = cursor.getString(cursor.getColumnIndex(DBConstants.AddressColumn));
                    double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBConstants.LatColumn)));
                    double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBConstants.LngColumn)));
               //     String photo = cursor.getString(cursor.getColumnIndex(DBConstants.imageColumn));

                    places.add(new Place(name,formatted_address,lat,lng,"",vicinity));





                }
                myPlacesAdapter = new MyPlacesAdapter(places, getActivity(),(FragmentChanger)getActivity(), lat, lng);
                myRV.setAdapter(myPlacesAdapter);
            }else {
                Toast.makeText(getActivity(), "first time app", Toast.LENGTH_SHORT).show();
            }

        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

            if (places != null) {
                outState.putParcelableArrayList("places", places);
                outState.putDouble("lat", lat);
                outState.putDouble("lng", lng);

            }

    }
}


