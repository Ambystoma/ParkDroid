package com.parkingdroid.parkingdroid.Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Locale;


public class Fragment_Parking extends Fragment implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private TextView adress;
    private TextView distance;
    private Float distancenumber;
    private SharedPreferences mPref;
    private GoogleApiClient mGoogleApiClient;
    private Location parkLocation;
    private LocationRequest mLocationRequest;
    private int currentapiVersion =android.os.Build.VERSION.SDK_INT;

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

        if (mPref.contains(Constants.SP_PARK_LATITUDE) && mPref.contains((Constants.SP_PARK_LONGITUDE))) {


            parkLocation.setLatitude(Utils.getDouble(mPref, Constants.SP_PARK_LATITUDE, 0.0));
            parkLocation.setLongitude(Utils.getDouble(mPref, Constants.SP_PARK_LONGITUDE, 0.0));

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = new ArrayList<>();

            try {
                addresses = geocoder.getFromLocation(parkLocation.getLatitude(), parkLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }


            StringBuilder allAddress = new StringBuilder();

            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex() - 1; i++) {
                    allAddress.append(addresses.get(0).getAddressLine(i));
                    allAddress.append(" ");
                }
            }

            adress.setText(allAddress);

            LatLng latLng = new LatLng(parkLocation.getLatitude(), parkLocation.getLongitude());


            ((MainActivity) getActivity()).app.setAdresses(allAddress.toString());
            ((MainActivity) getActivity()).app.setDates(new Date().toString());
            ((MainActivity) getActivity()).app.setLatlongs(latLng.toString());


        }else{
            adress.setText("No es dispossa d'una darrera adreça d'aparcament coneguda");
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

            MainActivity mainactivity = (MainActivity) getActivity();
            if (mainactivity.mLocation == null) mainactivity.mLocation = new Location("hi");
            mainactivity.mLocation = location;

            if (location != null && location.getLongitude() !=  0.0 && location.getLatitude() != 0.0 ) {
               try {
                   distancenumber = location.distanceTo(parkLocation) / 1000;
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

        if (mGoogleApiClient != null){
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
}
