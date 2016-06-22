package com.parkingdroid.parkingdroid.Services;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Switch;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Models.Gactivity;
import com.parkingdroid.parkingdroid.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";
    private Integer cien = 100;

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

        if (parkDetecction(hola)){
            localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
         //   Log.i("Still", String.valueOf(hola.getStill()));
         //   Log.i("OnBike", String.valueOf(hola.getBicy()));
         //   Log.i("Cotxe", String.valueOf(hola.getCotxe()));
         //   Log.i("Run", String.valueOf(hola.getCorrent()));
        }

    }

    private boolean parkDetecction (Gactivity hola){

        List<Gactivity> one = new ArrayList<>();
        List<Gactivity> two = new ArrayList<>();

       // int var = hola.getStill();

      //  boolean d = var == 100 ? true : false;
//TODO: CHANGE THIS LINE
        //if (true){
            //only for testing
          //  return true;

        if (hola.getCotxe()==100){
                one.add(hola);

        }else{
           if (one.size() >= 5){

                if (Utils.isBetween(two.size(),0,4)){

                    two.add(hola);

                } else if (two.size() == 5){

                   return true;

                } else {

                    one.clear();
                    two.clear();
                }
            }
        }

        return false;
    }

}