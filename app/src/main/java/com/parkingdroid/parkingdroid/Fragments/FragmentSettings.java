package com.parkingdroid.parkingdroid.Fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.Constants;
import com.parkingdroid.parkingdroid.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class FragmentSettings extends Fragment {

    private Switch trakingService;
    private Context context;
    private SharedPreferences mPrefs;
    private Switch trakingGeofence;
    private Spinner spinner;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        trakingService = (Switch) view.findViewById(R.id.settings_switch1);
        trakingGeofence = (Switch) view.findViewById(R.id.settings_switch2);
        spinner = (Spinner) view.findViewById(R.id.Bluetoothdevices);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor prefsEditor = mPrefs.edit();


        if ( !mPrefs.contains("trakingService") || mPrefs.getBoolean("trakingService",false)){
            trakingService.setChecked(true);
        }else{
            trakingService.setChecked(false);
        }

        if (((MainActivity)getActivity()).app.isGeofence()){
            trakingGeofence.setChecked(true);
        }else{
            trakingGeofence.setChecked(false);
        }

        trakingService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // ((OnNewsItemSelectedListener) context).onSettingsPicked(trakingService.isActivated());

                boolean trak = trakingService.isChecked();

                prefsEditor.putBoolean ("trakingService" , trak);

                prefsEditor.apply();

                if (trak){
                    ((MainActivity) getActivity()).UpdatesHandler();
                }else{
                    ((MainActivity) getActivity()).removeUpdatesHandler();
                }

            }
        });

        trakingGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trakingGeofence.isChecked()){
                    ((MainActivity) getActivity()).app.setGeofence(true);
                }else{
                    ((MainActivity) getActivity()).app.setGeofence(false);
                }
            }
        });

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        s.add("Select your Car bluetooth favorite device");
        for(BluetoothDevice bt : pairedDevices)
            s.add(bt.getName());

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, s){

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view1 = super.getView(position, convertView, parent);
                        View view2 = convertView;
                        if (position == 0){
                            TextView textView = (TextView) view1.findViewById(android.R.id.text1);
                            textView.setTextColor(getResources().getColor(R.color.colorGreyHint));
                        }

                        return view1;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view1 = super.getDropDownView(position, convertView, parent);

                        if (position == 0){
                            TextView textView = (TextView) view1.findViewById(android.R.id.text1);
                            textView.setTextColor(getResources().getColor(R.color.colorGreyHint));
                        }

                        return view1;
                    }
                };

        spinner.setAdapter(itemsAdapter);

        String selectedBT = mPrefs.getString(Constants.SP_BTDEVICE,"heu");

        if (selectedBT != null && selectedBT != "heu"){
            int i = 0;
            do{

                if (s.get(i).contains(selectedBT)){
                    spinner.setSelection(i);
                    break;
                }
                i++;
            }while (i<s.size());

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefsEditor.putString(Constants.SP_BTDEVICE,parent.getItemAtPosition(position).toString());
                prefsEditor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prefsEditor.putString(Constants.SP_BTDEVICE,"0");
                prefsEditor.commit();
            }
        });

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public interface OnNewsItemSelectedListener{
        public void onSettingsPicked(boolean servicesActivity);
    }
}
