package com.parkingdroid.parkingdroid.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class Fragment_Parking extends Fragment implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private TextView adress;
    private TextView distance;
    private Float distancenumber;
    private SharedPreferences mPref;
    private GoogleApiClient mGoogleApiClient;
    private Location parkLocation;
    private LocationRequest mLocationRequest;
    private int currentapiVersion =android.os.Build.VERSION.SDK_INT;
    private FloatingActionButton shareButton;

    //TODO: try mGoogleApliCLient.blokingconnect if bat consumption is to high

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.isNetworkAvailable(getContext())) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .build();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking,container, false);

        Location parkLocation = new Location("Park");
        adress = (TextView) view.findViewById(R.id.Carrer);
        distance = (TextView) view.findViewById(R.id.distancia);

        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = mPref.edit();

        shareButton = (FloatingActionButton) view.findViewById(R.id.share_button);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adress.getText() != null) {
                    String ad = adress.getText().toString();
                    if (ad.equals(getString(R.string.parking_NoAdress)) || ad.equals("")) {
                        displayPopupWindow(shareButton);
                    }else{
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, adress.getText().toString());
                        startActivity(Intent.createChooser(sharingIntent, "Share link using"));
                    }
                }
            }
        });

        if (mPref.contains(Constants.SP_PARK_LATITUDE) && mPref.contains((Constants.SP_PARK_LONGITUDE))) {


            parkLocation.setLatitude(Utils.getDouble(mPref, Constants.SP_PARK_LATITUDE, 0.0));
            parkLocation.setLongitude(Utils.getDouble(mPref, Constants.SP_PARK_LONGITUDE, 0.0));

            adress.setText(createadress(parkLocation.getLatitude(),parkLocation.getLongitude()));

        }else{
            adress.setText(R.string.parking_NoAdress);
        }

        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocatcion();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (mPref.contains(Constants.SP_PARK_LATITUDE) && mPref.contains((Constants.SP_PARK_LONGITUDE))) {

            Location location1 = new Location("Park");

            location1.setLatitude(Utils.getDouble(mPref, Constants.SP_PARK_LATITUDE, 0.0));
            location1.setLongitude(Utils.getDouble(mPref, Constants.SP_PARK_LONGITUDE, 0.0));

            if (location != null && location.getLongitude() !=  0.0 && location.getLatitude() != 0.0 ) {
               try {
                   distancenumber = location.distanceTo(location1) / 1000;
               }catch (Exception e){
                   distancenumber = 0.0f;
               }
            } else {
                distancenumber = 0.0f;
            }

            distance.setText("Distancia Aproximada " + String.format("%.2f", distancenumber) + " Km.");
        }else{
            distance.setText("");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();}

    }

    public void getLocatcion(){

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mGoogleApiClient.isConnected()) {

            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Note that this can be NULL if last location isn't already known.
            if (mCurrentLocation != null) {
                // Print current location if not null
                Log.i("LOCATION", "current location: " + mCurrentLocation.toString());
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }
            // Begin polling for new location updates.
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(0)
                    .setNumUpdates(1);

            // Request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    private void displayPopupWindow(View anchorView) {

        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.popup_nothingtosend, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);
    }

    private String createadress(double latitude, double longitude){


        try {
            String hola = new GetNameStreet().execute(latitude,longitude).get();
            return hola;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;

    }

    private class GetNameStreet extends AsyncTask<Double,Void,String>{

        @Override
        protected String doInBackground(Double... params) {

          /*  Set <String> set = new HashSet<>();
            set = mPref.getStringSet(Constants.SP_ADRESS_LIST,new HashSet<String>());
            List<String> mList = new ArrayList<>();
            mList.addAll(set);*/


            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = new ArrayList<>();

            try {
                addresses = geocoder.getFromLocation(params[0], params[1], 1);
            } catch (IOException e) {
                e.printStackTrace();
            }


            StringBuilder allAddress = new StringBuilder();

            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex() - 1; i++) {
                    allAddress.append(addresses.get(0).getAddressLine(i));
                    allAddress.append(" ");
                }
                return allAddress.toString();
            }else{

                return new LatLng(params[0],params[1]).toString();
            }




        }
    }

}
