package com.parkingdroid.parkingdroid.Fragments;



import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Utils;


public class MainActivityFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int currentapiVersion =android.os.Build.VERSION.SDK_INT;
    final FragmentIndoor fragmentIndoor = new FragmentIndoor();
    final FragmentMap fragmentMap = new FragmentMap();
    final Fragment_Parking fragment_parking = new Fragment_Parking();
    private SharedPreferences mPrefs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mainactivityfragment,container,false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {

                switch (position){
                    case 0:
                        return fragment_parking ;

                    case 1:
                        return fragmentIndoor;

                    case 2:
                        return fragmentMap;

                    default:
                        return null;

                }

            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                switch (position){
                    case 0:
                        return getString(R.string.Tabbar_Parking);
                    case 1:
                        return getString(R.string.Tabbar_Indoor);
                    case 2:
                        return getString(R.string.Tabbar_Maps);
                    default:
                        return null;
                }
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        if (Utils.isNetworkAvailable(getContext())) {
            //GoogleApiClient for location
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) getContext())
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) getContext()).build();
        }

        return view;
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
                    .setInterval(Constants.LOCATION_INTERVAL)
                    .setNumUpdates(1)
                    .setFastestInterval(Constants.LOCATION_FASTEST_INTERVAL);

            // Request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);



        }else{

            if (currentapiVersion >= Build.VERSION_CODES.M) {
                Utils.checkAllPermissions(getContext(),getActivity());
            }

        }

    }

    @Override
    public void onLocationChanged(Location location) {

        ((MainActivity) getActivity()).mLocation = location;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        SharedPreferences.Editor editor = mPrefs.edit();

        Utils.putDouble(editor,Constants.SP_PARK_LATITUDE, location.getLatitude());
        Utils.putDouble(editor,Constants.SP_PARK_LONGITUDE,location.getLongitude());


        //editor.putString("ParkLat", ((float) location.getLatitude()));
        //editor.putFloat("ParLong", (float) location.getLongitude());

        editor.apply();

        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Log.i("LOCATION", msg.toString());
        Toast.makeText(getActivity(),String.valueOf(latLng.latitude),Toast.LENGTH_LONG).show();*/



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {

       // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();}
        super.onStop();
    }
}
