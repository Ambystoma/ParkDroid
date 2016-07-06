package com.parkingdroid.parkingdroid;

import android.app.Activity;

import com.google.android.gms.location.Geofence;


public final class Constants {

    private Constants() {
    }

    public static final String TAG = "PardroidApp";

    public static final int LOCATION_COARSE_REQUEST = 2;
    public static final int LOCATION_FINE_REQUEST = 1;
    public static final int INTERNET = 0;

    public static final String PACKAGE_NAME = "com.parkingdroid.parkingdroid";
    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    public static final String GEOFENCE_INTENT = PACKAGE_NAME + ".GEOFENCE_INTENT";
    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final String  BEACON_EXTRA = PACKAGE_NAME + ".BEACON_EXTRA";

    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;
    public static final int LOCATION_INTERVAL = 0;
    public static final int LOCATION_FASTEST_INTERVAL = 0;

    public static final String FIRST_FRAGMENT = "first_fragment";
    public static final String SECOND_FRAGMENT = "second_fragment";
    public static final String THIRD_FRAGMENT = "third_fragment";
    public static final String FOURTH_FRAGMENT = "fourth_fragment";

    //Shared preferences notation
    public static final String SP_PARK_LATITUDE = "ParkLat";
    public static final String SP_PARK_LONGITUDE = "ParkLong";
    public static final String SP_ADRESS = "AdressPark";
    public static final String SP_ADRESS_LIST = "AdressPark";
    public static final String SP_ADRESS_DATE_LIST = "AdressDatePark";
    public static final String SP_ADRESS_DATE_LIST2 = "AdressDateParkdos";
    public static final String SP_LATLONG_LIST = "LatlongPark";
    public static final String SP_ISLOGED = "islogued";
    public static final String SP_ENTERGEO = "entergeo";
    public static final String SP_INDOORPARK = "indoorpark";
    public static final String SP_BTDEVICE = "devise";

    // An app with dynamically-created geofences would want to include a reasonable expiration time.
    public static final long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;

    // Geofence parameters for LaSalle Campus.
    public static final String LASALLE_BUILDING_ID = "1";
    public static final double LASALLE_BUILDING_LATITUDE = 41.408452;
    public static final double LASALLE_BUILDING_LONGITUDE = 2.129870;
    public static final float LASALLE_BUILDING_RADIUS_METERS = 500.0f;

    // Geofence parameters for My home.
    public static final String HOME_ID = "2";
    public static final double HOME_LATITUDE = 41.489962;
    public static final double HOME_LONGITUDE = 2.352421;
    public static final float HOME_METERS = 50.0f;


}
