package com.parkingdroid.parkingdroid.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Models.Position;
import com.parkingdroid.parkingdroid.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FragmentHistoric extends Fragment {

    private ListView mlist;
    private ArrayList<String> mAdresss;
    private ArrayList<String> mDates;
    private ArrayList<String> mlatlongs;
    private ArrayList<Position> mPostions;
    private SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
    private TextView textView;
    private FloatingActionButton deleteButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_historic,container,false);

        mlist = (ListView) view.findViewById(R.id.historic_listView);
        textView = (TextView) view.findViewById(R.id.notdata);
        deleteButton = (FloatingActionButton) view.findViewById(R.id.delete_button);

        mAdresss = new ArrayList<String>();
        mDates = new ArrayList<String>();
        mlatlongs = new ArrayList<String>();
        mPostions = new ArrayList<Position>();

        mAdresss.addAll(((MainActivity) getActivity()).app.getAdresses());
        mDates.addAll(((MainActivity)getActivity()).app.getDates());
        mlatlongs.addAll(((MainActivity)getActivity()).app.getLatlongs());

        if (mAdresss.size()>0 && mDates.size()>0 && mlatlongs.size()>0) {

            textView.setVisibility(View.INVISIBLE);
            mPostions = creatingPosition(mAdresss, mDates, mlatlongs);

            ArrayAdapter<Position> adapter = new ArrayAdapter<Position>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, mPostions) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(mPostions.get(position).getStreet());
                    text2.setText(formatter.format(mPostions.get(position).getDate()));

                    return view;
                }
            };

            mlist.setAdapter(adapter);
        }else{

            textView.setVisibility(View.VISIBLE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    public ArrayList<Position> creatingPosition(ArrayList<String> adresss, ArrayList<String> dates, ArrayList<String> latlongs ){

        ArrayList<Position> p = new ArrayList<Position>();

        Position pos;


        for (int i = 0 ; i<adresss.size(); i++){

            String IU = latlongs.get(i);
            if (IU.contains("/")) {

                IU = IU.replace("lat/lng: (","");
                IU = IU.replace(")","");

            }

            String[] latlong =  IU.split(",");

            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);

            try {
                pos = new Position(latitude,longitude,adresss.get(i), formatter.parse(dates.get(i)));
            } catch (ParseException e) {
                pos = new Position(latitude,longitude,adresss.get(i), new Date());
            }
            p.add(pos);
        }

        return p;

    }

}
