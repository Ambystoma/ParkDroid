package com.parkingdroid.parkingdroid.Fragments;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.parkingdroid.parkingdroid.R;


public class FragmentMap extends SupportMapFragment {

    public static final String TAG = "MapFragment";
    private Location location;
    private GoogleApiClient mClient;
    private GoogleMap mMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                UpdateUI();
            }
        });

    }


    private void UpdateUI(){
        LatLng itemPoint = new LatLng(41.506687,2.389068);
        LatLng itemPoint2 = new LatLng(41.490314,2.357685);

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
                .build();

        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds,500);
        mMap.animateCamera(update);

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

