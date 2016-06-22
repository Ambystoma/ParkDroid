package com.parkingdroid.parkingdroid.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class GeofenceService extends IntentService {



    public GeofenceService() {
        super(GeofenceService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        Intent localIntent = new Intent(Constants.GEOFENCE_INTENT);

        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            showToast(this, R.string.geofence_error);
        } else {
            //Transition type
            int transitionType = geoFenceEvent.getGeofenceTransition();


            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {

                //TODO: send notification when enter and change UI

                List triggeringGeofences =  geoFenceEvent.getTriggeringGeofences();

                localIntent.putExtra(Constants.ACTIVITY_EXTRA, triggeringGeofences.get(0).toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {

                //todo:change UI
                localIntent.putExtra(Constants.ACTIVITY_EXTRA, "NULL");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

            }
        }
    }

    private void showToast(final Context context, final int resourceId) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
