package com.parkingdroid.parkingdroid.Activities;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Fragments.FragmentIndoor;
import com.parkingdroid.parkingdroid.Fragments.FragmentLogin;
import com.parkingdroid.parkingdroid.Fragments.FragmentMain_Historic;
import com.parkingdroid.parkingdroid.Fragments.FragmentSettings;
import com.parkingdroid.parkingdroid.Fragments.MainActivityFragment;
import com.parkingdroid.parkingdroid.Global;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Services.CarBluetoothService;
import com.parkingdroid.parkingdroid.Services.DetectedActivitiesIntentService;
import com.parkingdroid.parkingdroid.Services.DetectedActivitiesIntentService2;
import com.parkingdroid.parkingdroid.Utils;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status>,
        FragmentSettings.OnNewsItemSelectedListener
{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView mTitle;
    private int currentapiVersion =android.os.Build.VERSION.SDK_INT;
    private GoogleApiClient mActivityGoogleApiClient;
    public  Location mLocation;
    private ArrayList<DetectedActivity> updatedActivities;
    public List<Location> Locations;
    public boolean activitiesOK;
    private SharedPreferences mPrefs;
    private SharedPreferences mPrefs2;
    private Fragment mainActivityFragment;
    private Fragment currentFragment;
    public android.support.v4.app.FragmentManager fragmentManager;
    public android.support.v4.app.FragmentTransaction fragmentTransaction;
    public Global app;



    /**
     * A receiver for DetectedActivity objects broadcast by the
     * {@code ActivityDetectionIntentService}.
     */
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = (NavigationView) findViewById(R.id.navigationview);
        // Setup drawer view
        setupDrawerContent(navigationView);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.Toolbar_parkingdroid);

        // Find our drawer view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle);

        //Instanciate globals
        if (app == null) app = new Global(this);

        //calling FragmentManager
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (Utils.isNetworkAvailable(this)) {

            //GoogleApiClient for location
           /* mLocationGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    //.addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();*/

            buildActivityRecognitionGoogleApiClient();

            mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

            mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            mPrefs2 = getSharedPreferences(Constants.SP_ADRESS,MODE_PRIVATE);

        }else{
            Toast.makeText(this, "It's impossible know where you park without Connection",Toast.LENGTH_SHORT).show();
        }
        //Adding Fragment
        //MainActivityFragment mainActivityFragment = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.flContent);
        //Fragment mainActivityFragment = getSupportFragmentManager().findFragmentByTag(FIRST_FRAGMENT);

        mainActivityFragment = getSupportFragmentManager().findFragmentByTag(Constants.FIRST_FRAGMENT);

        if (mainActivityFragment == null){

            mainActivityFragment = new MainActivityFragment();

           /* android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();*/
            fragmentTransaction.add(R.id.flContent,mainActivityFragment,Constants.FIRST_FRAGMENT);
            fragmentTransaction.commit();


        }
        //Request Permissions
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            Utils.checkAllPermissions(this,this);
        }

        View headerview = navigationView.getHeaderView(0);
        LinearLayout ll = (LinearLayout) headerview.findViewById(R.id.header_drawer);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPrefs2.getBoolean(Constants.SP_ISLOGED,false)){
                   //todo:setdaprofileactivity
                }else{
                    selectFragment(FragmentLogin.class);
                }

                drawerLayout.closeDrawers();

            }
        });

        if (mPrefs2.getBoolean(Constants.SP_ISLOGED,false)){

            //todo:set username in da header drawer
        }

        startService(new Intent(MainActivity.this, CarBluetoothService.class));

    }
    

    @Override
    protected void onStart() {
        super.onStart();

        if (mActivityGoogleApiClient != null) mActivityGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // mLocationGoogleApiClient.disconnect();
        if (mActivityGoogleApiClient!= null){mActivityGoogleApiClient.disconnect();}

    }

    // Callback with the request from calling checkAllPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == Constants.LOCATION_FINE_REQUEST){ // || requestCode == Constants.LOCATION_COARSE_REQUEST || requestCode == Constants.INTERNET) {
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               //Permissions ok!
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("I can't locate your car without permissions, please give me your permission")
                        .setTitle("Consider give me permissions");
                builder.setNegativeButton("Go out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                });
                builder.setPositiveButton("Other Chance", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Utils.checkAllPermissions(MainActivity.this, MainActivity.this);

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        currentFragment = null;
        Class fragmentClass = null;
        String TAG = null;

        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = MainActivityFragment.class;
                mTitle.setText("ParkDroid");
                TAG = Constants.FIRST_FRAGMENT;
                break;
            case R.id.nav_second_fragment:

                if (mPrefs2.getBoolean(Constants.SP_ISLOGED,false)){
                    fragmentClass = FragmentMain_Historic.class;
                }else{
                    fragmentClass = FragmentLogin.class;
                }

                TAG = Constants.SECOND_FRAGMENT;
                /*fragmentClass = FragmentLogin.class;
                mTitle.setText(R.string.Drawer_Historic);
                TAG = Constants.SECOND_FRAGMENT;*/
                break;
            case R.id.nav_third_fragment:
                fragmentClass = FragmentIndoor.class;
                mTitle.setText("Indoor");
                TAG = Constants.THIRD_FRAGMENT;
                break;
            case R.id.nav_fourth_fragment:
                fragmentClass = FragmentSettings.class;
                mTitle.setText(R.string.Drawer_Settings);
                TAG = Constants.FOURTH_FRAGMENT;
                break;
            default:
                fragmentClass = MainActivityFragment.class;
        }

        try {
            if (fragmentClass != null){currentFragment = (Fragment) fragmentClass.newInstance();}
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace((R.id.flContent), currentFragment,TAG);
        fragmentTransaction.commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
   //when GoogleApiClient is connected..
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //boolean con = mPrefs.getBoolean("trakingService",false);

        if ( !mPrefs.contains("trakingService") || mPrefs.getBoolean("trakingService",false)){
            UpdatesHandler();
        }else{
            removeUpdatesHandler();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }


    protected synchronized void buildActivityRecognitionGoogleApiClient() {

        mActivityGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

            // Register the broadcast receiver that informs this activity of the DetectedActivity
            // object broadcast sent by the intent service.
            LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constants.BROADCAST_ACTION));

    }

    @Override
    protected void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onSettingsPicked(boolean servicesActivity) {
        activitiesOK = servicesActivity;

    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            updatedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);


            if (updatedActivities != null ){

            //todo set location in a global

             //   MainActivityFragment mainActivityFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(Constants.FIRST_FRAGMENT);

              //  if (mainActivityFragment != null) {
              //      mainActivityFragment.getLocatcion();
              //  }


                updatedActivities = null;
            }
        }
    }

    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} starts receiving callbacks when
     * activities are detected.
     */

    public void UpdatesHandler() {

        if (!mActivityGoogleApiClient.isConnected()) {

          //  Toast.makeText(this, R.string.not_connected,Toast.LENGTH_SHORT).show();
            mActivityGoogleApiClient.connect();

            return;
        }
        if (getActivityDetectionPendingIntent() != null) {
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    mActivityGoogleApiClient,
                    Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                    getActivityDetectionPendingIntent()
            ).setResultCallback(this);
        }
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     */
    public void removeUpdatesHandler() {
        if (!mActivityGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mActivityGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }else if (currentapiVersion >= Build.VERSION_CODES.M) {
                Utils.checkAllPermissions(this,this);
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if(resultCode == Activity.RESULT_OK){

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this,"If you want to get indoor location it's important to turn on Bluetooth",Toast.LENGTH_SHORT).show();
            }
    }

    public void selectFragment (Class fragmentClass){

        String TAG = "";

        switch (fragmentClass.getSimpleName()){
            case "FragmentMain_Historic":
                TAG = Constants.SECOND_FRAGMENT;

        }

        try {
            currentFragment = (Fragment) fragmentClass.newInstance();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace((R.id.flContent), currentFragment,TAG);
            fragmentTransaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


}
