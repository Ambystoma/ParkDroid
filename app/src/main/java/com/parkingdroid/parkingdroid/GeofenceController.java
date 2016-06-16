package com.parkingdroid.parkingdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.gson.Gson;

/**
 * Created by hectorvintromarti on 14/06/16.
 */
public class GeofenceController {

    private final String TAG = GeofenceController.class.getName();

    private Context context;
    private GoogleApiClient googleApiClient;
    private Gson gson;
    private SharedPreferences prefs;

   // private List<NamedGeofence> namedGeofences;
  //  public List<NamedGeofence> getNamedGeofences() {
     //   return namedGeofences;
  //  }

   // private List<NamedGeofence> namedGeofencesToRemove;

    private Geofence geofenceToAdd;
  //  private NamedGeofence namedGeofenceToAdd;

    private static GeofenceController INSTANCE;

    public static GeofenceController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeofenceController();
        }
        return INSTANCE;
    }
}
