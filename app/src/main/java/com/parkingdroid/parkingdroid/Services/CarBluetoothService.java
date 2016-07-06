package com.parkingdroid.parkingdroid.Services;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.parkingdroid.parkingdroid.Constants;

/**
 * Created by hectorvintromarti on 06/07/16.
 */
public class CarBluetoothService extends Service {

    private SharedPreferences mPref;
    private boolean CarBluertoothdevice;
    private IntentFilter filter1;
    private IntentFilter filter3;

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        this.registerReceiver(mReceiver2, filter1);
        this.registerReceiver(mReceiver2, filter3);
    }

    private final BroadcastReceiver mReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // When discovery finds a device
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                // Get the BluetoothDevice object from the Intent

                if (device.getName().equals(mPref.getString(Constants.SP_BTDEVICE,"nullis"))){
                    CarBluertoothdevice = true;
                }

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                if (CarBluertoothdevice) {
                    CarBluertoothdevice = false;
                    startService(new Intent(CarBluetoothService.this,DetectedActivitiesIntentService2.class));
                }
            }

        }

    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver2);
    }
}
