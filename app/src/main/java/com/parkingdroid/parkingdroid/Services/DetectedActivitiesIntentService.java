package com.parkingdroid.parkingdroid.Services;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Models.Gactivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final String TAG2 = "Dots_And_Loops";


    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */

    @Override
    protected void onHandleIntent(Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();
        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.

        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        Gactivity hola = new Gactivity();

        for (DetectedActivity da: detectedActivities) {

            if (da.getType() == da.STILL) hola.setStill(da.getConfidence());
            if (da.getType() == da.ON_BICYCLE) hola.setBicy(da.getConfidence());
            if (da.getType() == da.IN_VEHICLE) hola.setCotxe(da.getConfidence());
            if (da.getType() == da.WALKING) hola.setWalking(da.getConfidence());
            if (da.getType() == da.ON_FOOT) hola.setOnfoot(da.getConfidence());
            if (da.getType() == da.TILTING) hola.setInclinat(da.getConfidence());
            if (da.getType() == da.RUNNING) hola.setCorrent(da.getConfidence());
            if (da.getType() == da.UNKNOWN) hola.setNoze(da.getConfidence());

        }

        if (parkDetecction(hola)) {
            startService(new Intent(DetectedActivitiesIntentService.this, DetectedActivitiesIntentService2.class));
        }
/*
        Log.i(TAG2, String.valueOf(hola.getStill()));
        Log.i(TAG2, String.valueOf(hola.getBicy()));
        Log.i(TAG2, String.valueOf(hola.getCotxe()));
        Log.i(TAG2, String.valueOf(hola.getCorrent()));
        Log.i(TAG2, String.valueOf(hola.getOnfoot()));
        Log.i(TAG2, String.valueOf(hola.getInclinat()));
        Log.i(TAG2, String.valueOf(hola.getNoze()));
*/
        //todo:return somethinf to mainactivity
       //localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    //Todo:improve parkdetection
    private boolean parkDetecction (Gactivity hola){

        Set<String> one;
        Set<String> two;
        Long date = mPref.getLong("date", 0L);
        boolean isdriving = mPref.getBoolean("isdriving",false);

  //      if (true){return true;}

        if (mPref.getStringSet("one",null) != null){
            one = mPref.getStringSet("one",null);
        }else{
            one = new HashSet<>();
        }
        if (mPref.getStringSet("two", null) != null){
            two = mPref.getStringSet("two",null);
        }else{
            two = new HashSet<String>();
        }


        if (hola.getCotxe() >= 85 && !isdriving){
            one.add(hola.toString());
            if (date == 0L){
                date = new Date().getTime();
            }
        }

        if (hola.getCotxe() >= 30 && !isdriving){

            one.add(hola.toString());
            if (date != 0L && TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - date) >= 1 ){
                isdriving = true;
            }

        }else if (isdriving) {

            if (hola.getOnfoot() >= 80 || hola.getWalking() >= 80 || hola.getBicy() == 100){
                two.add(hola.toString());
            }

            if (two.size() >= 5){
                isdriving=false;
                one.clear();
                two.clear();
                date = 0L;
                save_preferences(one,two,date,isdriving);
                return true;
            }

        }

        if (date != 0L && TimeUnit.MILLISECONDS.toHours(new Date().getTime() - date) > 5){
            isdriving = false;
            date = 0L;
            one.clear();
            two.clear();
        }
        save_preferences(one,two,date,isdriving);
        return false;
    }

    private void save_preferences(Set<String> one, Set <String> two, Long date, boolean isdriving){

        editor.putStringSet("one",one);
        editor.putStringSet("two", two);
        editor.putBoolean("isdriving",isdriving);
        editor.putLong("date",date);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // Log.d(TAG,"onDestroy");

    }

   /* public void getLocatcion(){

        if (mGoogleApiClient.isConnected()) {

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

        }

    }*/


}