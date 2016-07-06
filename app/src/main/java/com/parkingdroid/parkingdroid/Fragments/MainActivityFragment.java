package com.parkingdroid.parkingdroid.Fragments;



import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Models.GeofenceObject;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Services.GeofenceService;
import com.parkingdroid.parkingdroid.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.parkingdroid.parkingdroid.Constants.HOME_ID;
import static com.parkingdroid.parkingdroid.Constants.HOME_LATITUDE;
import static com.parkingdroid.parkingdroid.Constants.HOME_LONGITUDE;
import static com.parkingdroid.parkingdroid.Constants.HOME_METERS;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_ID;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_LATITUDE;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_LONGITUDE;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_RADIUS_METERS;
import static com.parkingdroid.parkingdroid.Constants.TAG;


public class MainActivityFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int currentapiVersion =android.os.Build.VERSION.SDK_INT;
    final FragmentMap fragmentMap = new FragmentMap();
    final Fragment_Parking fragment_parking = new Fragment_Parking();
    private SharedPreferences mPrefs;
    private GeofenceObject mLasalleBuildingGeofence;
    private GeofenceObject mHomeGeofence;
    List<Geofence> mGeofenceList;
    private GoogleApiClient mApiClient;
    private PendingIntent mGeofenceRequestIntent;



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
                        return fragmentMap;

                    default:
                        return null;

                }

            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                switch (position){
                    case 0:
                        return getString(R.string.Tabbar_Parking);
                    case 1:
                        return getString(R.string.Tabbar_Maps);
                    default:
                        return null;
                }
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

       /* if (Utils.isNetworkAvailable(getContext())) {
            //GoogleApiClient for location
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) getContext())
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) getContext()).build();
        }*/

        if (Utils.isNetworkAvailable(getContext())) {
            mApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mApiClient.connect();
        }

        // Instantiate a new geofence storage area.
        // mGeofenceStorage = new SimpleGeofenceStore(this);
        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<Geofence>();
        createGeofences();

        return view;
    }

    public void getLocatcion(){

       /* if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
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

        }*/



    }

  /*  @Override
    public void onLocationChanged(Location location) {

        //((MainActivity) getActivity()).mLocation = location;

        //mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        //SharedPreferences.Editor editor = mPrefs.edit();

        //Utils.putDouble(editor,Constants.SP_PARK_LATITUDE, location.getLatitude());
        //Utils.putDouble(editor,Constants.SP_PARK_LONGITUDE,location.getLongitude());


        //editor.putString("ParkLat", ((float) location.getLatitude()));
        //editor.putFloat("ParLong", (float) location.getLongitude());

       // editor.apply();

        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Log.i("LOCATION", msg.toString());
        Toast.makeText(getActivity(),String.valueOf(latLng.latitude),Toast.LENGTH_LONG).show();

    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mApiClient.isConnected()) {

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else {
                if (mBluetoothAdapter.isEnabled()) {

                    LocationServices.GeofencingApi.addGeofences(mApiClient, mGeofenceList,
                            mGeofenceRequestIntent);
                }
            }

        }

        //Toast.makeText(getContext(), "Start Geofencing Service", Toast.LENGTH_SHORT).show();

    }

    private PendingIntent getGeofenceTransitionPendingIntent() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                try {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1332);
                }catch (Exception e){

                }
            }else{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Intent intent = new Intent(getContext(), GeofenceService.class);
                    return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                }else{
                    Toast.makeText(getContext(), "You need at least Lollipop, Sorry for the inconveniences", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return null;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStart() {
        super.onStart();

      //  if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
       // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
       // if (mGoogleApiClient != null){mGoogleApiClient.disconnect();}

    }

    public void createGeofences() {
        // Create internal "flattened" objects containing the geofence data.
        mLasalleBuildingGeofence = new GeofenceObject(
                LASALLE_BUILDING_ID,                // geofenceId.
                LASALLE_BUILDING_LATITUDE,
                LASALLE_BUILDING_LONGITUDE,
                LASALLE_BUILDING_RADIUS_METERS
        );
        mHomeGeofence = new GeofenceObject(
                HOME_ID,                // geofenceId.
                HOME_LATITUDE,
                HOME_LONGITUDE,
                HOME_METERS
        );

        // Store these flat versions in SharedPreferences and add them to the geofence list.
        //mGeofenceStorage.setGeofence(ANDROID_BUILDING_ID, mAndroidBuildingGeofence);
        //mGeofenceStorage.setGeofence(YERBA_BUENA_ID, mYerbaBuenaGeofence);
        mGeofenceList.add(mLasalleBuildingGeofence.toGeofence());
        mGeofenceList.add(mHomeGeofence.toGeofence());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.isGooglePlayServicesAvailable(getContext())) {
            Log.e(TAG, "Google Play services unavailable.");
            getActivity().finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(),
                        9000);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }


}
