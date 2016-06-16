package com.parkingdroid.parkingdroid.Fragments;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Fragment_Parking extends Fragment {

    private TextView adress;
    private TextView distance;
    private Float distancenumber;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking,container, false);


        Location parkLocation = new Location("hola");

        parkLocation.setLatitude(41.506687);
        parkLocation.setLongitude(2.389068);

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocation(parkLocation.getLatitude(),parkLocation.getLongitude() , 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        adress = (TextView) view.findViewById(R.id.Carrer);

        distance = (TextView) view.findViewById(R.id.distancia);

        StringBuilder allAddress = new StringBuilder();

        for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex()-1;i++){
            allAddress.append(addresses.get(0).getAddressLine(i));
            allAddress.append(" ");
        }

        adress.setText(allAddress);
        
        MainActivity mainactivity = (MainActivity) getActivity();

        if (mainactivity.mLocation != null) {
            distancenumber = mainactivity.mLocation.distanceTo(parkLocation) / 1000;
        }else {
            distancenumber = 0.0f;
        }

        distance.setText("Distancia Aproximada " + String.format("%.2f",distancenumber) + " Km.");

        return view;
    }
}
