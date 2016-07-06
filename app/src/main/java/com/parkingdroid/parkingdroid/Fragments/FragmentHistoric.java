package com.parkingdroid.parkingdroid.Fragments;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.Models.Position;
import com.parkingdroid.parkingdroid.R;
import com.parkingdroid.parkingdroid.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class FragmentHistoric extends Fragment {

    private ListView mlist;
    private ArrayList<String> mAdresss;
    private ArrayList<String> mAdresss2;
    private ArrayList<String> mDates;
    private ArrayList<String> mlatlongs;
    private ArrayList<Position> mPostions;
    //private SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
    private SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
    private TextView textView;
    private FloatingActionButton deleteButton;
    private SharedPreferences mPref;
    private ArrayAdapter<Position> adapter;
    private double latitude;
    private double longitude;
    private SharedPreferences.Editor editor;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_historic, container, false);

        mlist = (ListView) view.findViewById(R.id.historic_listView);
        textView = (TextView) view.findViewById(R.id.notdata);
        deleteButton = (FloatingActionButton) view.findViewById(R.id.delete_button);

        mAdresss = new ArrayList<String>();
        mAdresss2 = new ArrayList<String>();
        mDates = new ArrayList<String>();
        mlatlongs = new ArrayList<String>();
        mPostions = new ArrayList<Position>();
        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = mPref.edit();

        //mAdresss.addAll(((MainActivity) getActivity()).app.getAdresses());
        mAdresss.addAll(mPref.getStringSet(Constants.SP_ADRESS_LIST, new HashSet<String>()));
        mAdresss2.addAll(mPref.getStringSet(Constants.SP_ADRESS_DATE_LIST2, new HashSet<String>()));
        mDates.addAll(mPref.getStringSet(Constants.SP_ADRESS_DATE_LIST, new HashSet<String>()));
        //mlatlongs.addAll(((MainActivity)getActivity()).app.getLatlongs());
        mlist.setAdapter(null);


        if (mAdresss.size() > 0 && mDates.size() > 0) {

            textView.setVisibility(View.INVISIBLE);

            mPostions = creatingPosition(mAdresss2, mAdresss, mDates);

            adapter = new ArrayAdapter<Position>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, mPostions) {

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
        } else {

            textView.setVisibility(View.VISIBLE);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    adapter.clear();
                    mAdresss.clear();
                    mDates.clear();
                    adapter.notifyDataSetChanged();
                    editor.remove(Constants.SP_ADRESS_DATE_LIST);
                    editor.remove(Constants.SP_ADRESS_LIST);
                    editor.remove(Constants.SP_ADRESS_DATE_LIST2);
                    // editor.putStringSet()
                    // editor.putStringSet(Constants.SP_ADRESS_DATE_LIST,new HashSet<String>());
                    // editor.putStringSet(Constants.SP_ADRESS,new HashSet<String>());
                    editor.apply();

                }
            }
        });

        return view;
    }

    public ArrayList<Position> creatingPosition(ArrayList<String> adresss2, ArrayList<String> adresss, ArrayList<String> dates) {

        ArrayList<Position> p = new ArrayList<Position>();
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        Position pos;
        if (adresss.size() == adresss2.size()) {
            for (int i = 0; i < adresss.size(); i++) {

                String IU = adresss.get(i);
                String UI = adresss2.get(i);

                if (UI.indexOf("/") > 0 || UI.equals("") || Utils.isNumeric(UI)) {

                    IU = IU.replace("lat/lng: (", "");
                    IU = IU.replace(")", "");

                    String[] latlong = IU.split(",");

                    latitude = Double.parseDouble(latlong[0]);
                    longitude = Double.parseDouble(latlong[1]);

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (!addresses.equals("")) {
                            StringBuilder allAddress = new StringBuilder();

                            if (addresses.size() > 0) {
                                for (int j = 0; j < addresses.get(0).getMaxAddressLineIndex() - 1; j++) {
                                    allAddress.append(addresses.get(0).getAddressLine(j));
                                    allAddress.append(" ");
                                }
                            }
                            adresss.set(i, allAddress.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //todo:iiiiiiiget(i)
                    try {
                        pos = new Position(latitude, longitude, adresss.get(i), formatter.parse(dates.get(0)));
                    } catch (ParseException e) {
                        pos = new Position(latitude, longitude, adresss.get(i), new Date());
                    }


                } else {

                    try {
                        pos = new Position(latitude, longitude, adresss2.get(i), formatter.parse(dates.get(0)));
                    } catch (ParseException e) {
                        pos = new Position(latitude, longitude, adresss2.get(i), new Date());
                    }
                }

                p.add(pos);

            }
        }

        return p;

    }

}
