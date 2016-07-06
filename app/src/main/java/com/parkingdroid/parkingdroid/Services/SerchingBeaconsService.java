package com.parkingdroid.parkingdroid.Services;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Constants;

import java.util.List;
import java.util.UUID;

public class SerchingBeaconsService extends Service {

    SharedPreferences mPref;
    SharedPreferences.Editor editor;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mPref.edit();
        final BeaconManager beaconManager = new BeaconManager(getApplicationContext());
        final Region region1 = new Region(
                "monitored region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                2222, 1001);

        final Region region2 = new Region(
                "monitored region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                1111, 1001);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(region1);
                beaconManager.startMonitoring(region2);

            }});

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(final Region region, List<Beacon> list) {

                //relativeLayout.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        beaconManager.stopMonitoring(region);
                        beaconManager.stopMonitoring(region2);
                        beaconManager.disconnect();

                        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                            @Override
                            public void onServiceReady() {
                                beaconManager.startMonitoring(region1);
                                beaconManager.startMonitoring(region2);

                            }});

                        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
                            @Override
                            public void onEnteredRegion(final Region region, List<Beacon> list) {

                                //relativeLayout.setVisibility(View.GONE);
                                if (list.get(0).getMajor() == 2222) {
                                    showNotification(
                                            "We have your ParkPlace",
                                            "When you return, remmemer to ask us how to do it.");
                                    editor.putString(Constants.SP_INDOORPARK,"2222");
                                    editor.apply();


                                }else{

                                    showNotification(
                                            "You parked in HOME at 3 floor, Section White Whale",
                                            "Is you need more Park clues, remmember to visit PARKDROID");

                                    editor.putString(Constants.SP_INDOORPARK,"1111");
                                    editor.apply();
                                }

                                SendResults(list.get(0));

                                //Toast.makeText(getContext(),"SHOW PARKING PLACE!",Toast.LENGTH_SHORT).show();
                                //disconnect and restart service
                                beaconManager.stopMonitoring(region1);
                                beaconManager.stopMonitoring(region2);
                                beaconManager.disconnect();
                                stopService(new Intent(SerchingBeaconsService.this,SerchingBeaconsService.class));
                                startService(new Intent(SerchingBeaconsService.this,SerchingBeaconsService.class));
                            }
                            @Override
                            public void onExitedRegion(Region region) {
                                // could add an "exit" notification too if you want (-:
                                editor.putString(Constants.SP_INDOORPARK,"0000");
                                editor.apply();
                                beaconManager.stopMonitoring(region);
                                beaconManager.disconnect();
                            }
                        });

                    }
                }, 10000);

            }
            @Override
            public void onExitedRegion(Region region) {
                // could add an "exit" notification too if you want (-:
               // Toast.makeText(getContext(),"You are out the beacon area",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                    new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }

    private void SendResults (Beacon beacon){

        Intent localIntent = new Intent(Constants.GEOFENCE_INTENT);
        localIntent.putExtra(Constants.BEACON_EXTRA, beacon.getMajor());
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
