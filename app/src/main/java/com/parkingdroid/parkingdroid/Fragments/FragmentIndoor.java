package com.parkingdroid.parkingdroid.Fragments;


import static com.parkingdroid.parkingdroid.Constants.TAG;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_ID;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_LATITUDE;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_LONGITUDE;
import static com.parkingdroid.parkingdroid.Constants.LASALLE_BUILDING_RADIUS_METERS;
import static com.parkingdroid.parkingdroid.Constants.HOME_ID;
import static com.parkingdroid.parkingdroid.Constants.HOME_LATITUDE;
import static com.parkingdroid.parkingdroid.Constants.HOME_LONGITUDE;
import static com.parkingdroid.parkingdroid.Constants.HOME_METERS;
import static com.parkingdroid.parkingdroid.Constants.GEOFENCE_EXPIRATION_TIME;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Models.GeofenceObject;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Services.GeofenceService;
import com.parkingdroid.parkingdroid.Utils;

import java.util.ArrayList;
import java.util.List;

public class FragmentIndoor extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Internal List of Geofence objects. In a real app, these might be provided by an API based on
    // locations within the user's proximity.
    List<Geofence> mGeofenceList;

    // These will store hard-coded geofences in this sample app.
    private GeofenceObject mLasalleBuildingGeofence;
    private GeofenceObject mHomeGeofence;


    private LocationServices mLocationService;
    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;
    private GoogleApiClient mApiClient;
    private TextView title;

    //protected MainActivity.ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.isGooglePlayServicesAvailable(getContext())) {
            Log.e(TAG, "Google Play services unavailable.");
            getActivity().finish();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indoor,container,false);

        title = (TextView) view.findViewById(R.id.indoor_title);

        mApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        // Instantiate a new geofence storage area.
        // mGeofenceStorage = new SimpleGeofenceStore(this);
        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<Geofence>();
        createGeofences();


        return view;
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
    public void onConnected(@Nullable Bundle bundle) {

        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mApiClient.isConnected()) {

            LocationServices.GeofencingApi.addGeofences(mApiClient, mGeofenceList,
                    mGeofenceRequestIntent);

        }

        Toast.makeText(getContext(), "Start Geofencing Service", Toast.LENGTH_SHORT).show();

    }

    /**
     * Create a PendingIntent that triggers GeofenceTransitionIntentService when a geofence
     * transition occurs.
     */

    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(getContext(), GeofenceService.class);
        return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
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

    @Override
    public void onResume() {
        super.onResume();

        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,new IntentFilter(Constants.GEOFENCE_INTENT));
    }



    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ArrayList enterGeo = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String stringulis = intent.getStringExtra(Constants.ACTIVITY_EXTRA);

            if (stringulis.contains("id:2")){

                //todo: check if and show ui depending on geofence
                title.setText("You are in Home");

            }else if (stringulis.contains("id:1")){

                title.setText("You are in LA SALLE");

            }
        }
    };

    public void stopGeoServices(){

        if (null != mGeofenceRequestIntent && mApiClient !=null)  {
        LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);}

    }

    public void startGeoServices(){

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mApiClient.isConnected() && null != mGeofenceRequestIntent) {

            LocationServices.GeofencingApi.addGeofences(mApiClient, mGeofenceList,
                    mGeofenceRequestIntent);

        }

    }



}
