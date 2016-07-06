package com.parkingdroid.parkingdroid.Fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hectorvintromarti on 27/06/16.
 */
public class FragmentMap_Historic extends SupportMapFragment {

    public static final String TAG = "MapFragment";
    private Location location;
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    public Location park;
    public Location current;
    private SharedPreferences mPrefs;
    private List<String> historic;
    private List<LatLng> mlatitud;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();

        park = new Location("park");
        mlatitud = new ArrayList<>();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Set<String> set = new HashSet<>();
        set = mPrefs.getStringSet(Constants.SP_ADRESS_LIST,null);
        if (set != null) historic = new ArrayList<>(set);
        if (historic != null && historic.size() > 0) {
        for (String i: historic) {
            if (i.indexOf("/") > 0 || i.equals("")) {

                i = i.replace("lat/lng: (", "");
                i = i.replace(")", "");

                String[] latlong = i.split(",");

                //mlatitud.clear();
                mlatitud.add(new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1])));

            }
        }
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                UpdateUI();
            }
        });

    }}


    private void UpdateUI() {

        mMap.clear();
        LatLng itemPoint =  new LatLng(0.0,0.0);
        LatLng itemPoint2 = new LatLng(0.0,0.0);


        if (mlatitud.size() != 0.0) {

            for (LatLng i:mlatitud) {

                itemPoint = i;

                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.appicon);
                BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(icon);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(itemPoint)
                        .icon(itemBitmap);

                mMap.addMarker(markerOptions);
            }

            itemPoint2 = maxdiferece(mlatitud);

            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(itemPoint)
                    .include(itemPoint2)
                    .build();

            CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            mMap.animateCamera(update);

        }

    }
//todo: improve maxdiferece
    private LatLng maxdiferece(List<LatLng> start){

        double max = 0.0;
        int jj = -1;
        int ii = -1;
        int jjj = -1;
        int iii = -1;
        double aux1 = 0.0;
        double aux2 = 0.0;
        double aux3 = 0.0;
        double aux4 = 0.0;


        for (int i = 0; i < start.size()-1; i++){
            for (int j = 1; j < start.size(); j++){
                if (Math.abs(start.get(i).latitude - start.get(j).latitude)> max){
                    max = Math.abs(start.get(i).latitude - start.get(j).latitude);
                    jj = j;
                    ii = i;
                }
                if (Math.abs(start.get(i).longitude - start.get(j).longitude)> max){
                    max = Math.abs(start.get(i).longitude - start.get(j).longitude);
                    jjj = j;
                    iii = i;
                }
            }
        }

        if (ii != -1 && jj != -1) {
            for (LatLng i : start) {
                aux1 += Math.abs(i.latitude - start.get(ii).latitude);
                aux2 += Math.abs(i.latitude - start.get(jj).latitude);
                aux3 += Math.abs(i.longitude - start.get(iii).longitude);
                aux4 += Math.abs(i.longitude - start.get(jj).longitude);
            }

            if (aux1<= aux2) {
                aux1 = start.get(jj).latitude;
            }else{
                aux1 = start.get(ii).latitude;
            }

            if (aux3<= aux4){
                aux2 = start.get(jjj).longitude;
            }else{
                aux2 = start.get(iii).longitude;
            }
        }
        return new LatLng(aux1,aux2);
    }

}