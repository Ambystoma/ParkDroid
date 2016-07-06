package com.parkingdroid.parkingdroid.Fragments;




import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
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


public class FragmentMap extends SupportMapFragment {

    public static final String TAG = "MapFragment";
    private Location location;
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    public Location park;
    public Location current;
    private SharedPreferences mPrefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();

        park = new Location("park");

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        park.setLatitude(Utils.getDouble(mPrefs, Constants.SP_PARK_LATITUDE, 0.0));
        park.setLongitude(Utils.getDouble(mPrefs, Constants.SP_PARK_LONGITUDE, 0.0));


        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                UpdateUI(park,current);
            }
        });

    }


    private void UpdateUI(Location park, Location current){


        if (park.getLatitude() != 0.0 && park.getLongitude() != 0.0) {

            LatLng itemPoint = new LatLng(park.getLatitude(), park.getLongitude());
            LatLng itemPoint2 = new LatLng(park.getLatitude()+0.005, park.getLongitude()+0.005);
            LatLng itemPoint3 = new LatLng(park.getLatitude()-0.005, park.getLongitude()-0.005);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.appicon);
            BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(icon);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(itemPoint)
                    .icon(itemBitmap);

            mMap.clear();
            mMap.addMarker(markerOptions);

            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(itemPoint)
                    .include(itemPoint2)
                    .include(itemPoint3)
                    .build();

            CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 150);
            mMap.animateCamera(update);

        }

    }

   /* private class SearchTask extends AsyncTask<Location,Void,Void>{
        private Bitmap mbitmap;
        private Location mLocation;


        @Override
        protected Void doInBackground(Location... params) {


            //41.408675
            //2.129838
            mLocation=params[0];

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }*/


}

