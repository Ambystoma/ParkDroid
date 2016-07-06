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
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Global;
import com.parkingdroid.parkingdroid.Models.GeofenceObject;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Services.GeofenceService;
import com.parkingdroid.parkingdroid.Utils;
import com.estimote.sdk.BeaconManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private View view;
    private RelativeLayout relativeLayout;
    private  String enterGeo;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private CardView cardView;
    private TextView plasa;
    private TextView planta;


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
        view = inflater.inflate(R.layout.fragment_indoor,container,false);

        title = (TextView) view.findViewById(R.id.indoor_title);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.loadingPanel);
        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = mPref.edit();
        cardView = (CardView) view.findViewById(R.id.card);
        plasa = (TextView) view.findViewById(R.id.distancia);
        planta = (TextView) view.findViewById(R.id.planta);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_ecsplain);
        if (enterGeo == null) enterGeo =  mPref.getString(Constants.SP_ENTERGEO,null);

        if ( enterGeo != null && enterGeo.contains("id:2")){

            title.setText("You are in Home");
            linearLayout.setVisibility(View.GONE);
            beaconsResult();

        }else if (enterGeo != null && enterGeo.contains("id:1")){

            title.setText("You are in LA SALLE");
            linearLayout.setVisibility(View.GONE);
            beaconsResult();

        }else if (enterGeo == null && enterGeo.contains("NULL")){

            title.setText("You are not indoor");
            linearLayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

        }

        if (enterGeo == null || enterGeo.contains("NULL") ){
            cardView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
        }

        return view;
    }

    private void beaconsResult()
    {
        String indoorPark = mPref.getString(Constants.SP_INDOORPARK,"0");

         if (indoorPark.equals("0")){
            cardView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            cardView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

            if (indoorPark.equals("2222")){
                plasa.setText("IB3");
                planta.setText("5");
            }else if(indoorPark.equals("1111")){
                planta.setText("2");
                plasa.setText("HNC");
            }else{
                cardView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    private void beaconsResult2(Intent intent)
    {
        String indoorPark = mPref.getString(Constants.SP_INDOORPARK,"0");
        int beacon = intent.getIntExtra(Constants.BEACON_EXTRA,0);

        if (beacon == 0){
            cardView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            cardView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

            if (beacon == 2222){
                plasa.setText("IB3");
                planta.setText("5");
            }else if(beacon == 1111){
                planta.setText("2");
                plasa.setText("HNC");
            }else{
                cardView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();

        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
       // if (((MainActivity) getActivity()).app.isGeofence()) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.GEOFENCE_INTENT));
       // }

    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {


            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_ecsplain);

            if (enterGeo == null) enterGeo = intent.getStringExtra(Constants.ACTIVITY_EXTRA);

            if (!enterGeo.equals(mPref.getString(Constants.SP_ENTERGEO,null)))
            {
            if (enterGeo.contains("id:2")){

                title.setText("You are in Home");
                linearLayout.setVisibility(View.GONE);

            }else if (enterGeo.contains("id:1")){

                title.setText("You are in LA SALLE");
                linearLayout.setVisibility(View.GONE);


            }else if (enterGeo.contains("NULL")){

                title.setText("You are not indoor");
                linearLayout.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);

            }}
            beaconsResult2(intent);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        //stopGeoServices();
    }

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

    /*public void showNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Intent notifyIntent = new Intent(getContext(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(getContext(), 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getContext())
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
    }*/

    public void sendBroadCast(){
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.GEOFENCE_INTENT));
    }

}
