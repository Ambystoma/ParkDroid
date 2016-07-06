package com.parkingdroid.parkingdroid.Services;


import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Utils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;



public class DetectedActivitiesIntentService2 extends Service {

    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 10f;
    private LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
            };


    public DetectedActivitiesIntentService2() {
        // Use the TAG to name the worker thread.
        //super(TAG);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e(TAG, "onCreate");

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                   // mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            Utils.putDouble(editor,Constants.SP_PARK_LATITUDE, location.getLatitude());
            Utils.putDouble(editor,Constants.SP_PARK_LONGITUDE,location.getLongitude());
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            editor.putLong("parkDate", new Date().getTime());

            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

            Set <String> set = new HashSet<>();
            set = mPref.getStringSet(Constants.SP_ADRESS_LIST,new HashSet<String>());
            List<String> addresses = new ArrayList<>();
            addresses.addAll(set);
            addresses.add(latLng.toString());
            //set.clear();
            set= new HashSet<>(addresses);
            editor.putStringSet(Constants.SP_ADRESS_LIST,set);
            editor.apply();
            set.clear();
            set = mPref.getStringSet(Constants.SP_ADRESS_DATE_LIST2,new HashSet<String>());
            String adris = String.valueOf(new Date().getTime());
            ArrayList<String> deoplist = new ArrayList<>();
            deoplist.addAll(set);
            try {
                List<Address> addressess = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                StringBuilder allAddress = new StringBuilder();
                if (addressess.size() > 0) {
                    for (int j = 0; j < addressess.get(0).getMaxAddressLineIndex() - 1; j++) {
                        allAddress.append(addressess.get(0).getAddressLine(j));
                        allAddress.append(" ");
                    }
                }
                adris = allAddress.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            deoplist.add(adris);
            set= new HashSet<String>(deoplist);
            editor.putStringSet(Constants.SP_ADRESS_DATE_LIST2, set);
            editor.apply();
            deoplist.clear();set.clear();
            set = mPref.getStringSet(Constants.SP_ADRESS_DATE_LIST,new HashSet<String>());
            addresses.clear();
            addresses.addAll(set);
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
            String datetime = formatter.format(new Date());
            addresses.add(datetime);
            set.clear();
            set = new HashSet<>(addresses);
            editor.putStringSet(Constants.SP_ADRESS_DATE_LIST,set);

            editor.apply();

            if (mLocationManager != null) {
                for (int i = 0; i < mLocationListeners.length; i++) {
                    try {
                        mLocationManager.removeUpdates(mLocationListeners[i]);
                    } catch (Exception ex) {
                        Log.i(TAG, "fail to remove location listners, ignore", ex);
                    }
                }
            }

            //todo:return something to mainactivity
             Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
            //localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
//            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


}