package com.parkingdroid.parkingdroid.Fragments;

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
import android.widget.Switch;

import com.google.gson.Gson;
import com.parkingdroid.parkingdroid.Activities.MainActivity;
import com.parkingdroid.parkingdroid.R;

import java.util.Date;


public class FragmentSettings extends Fragment {

    private Switch trakingService;
    private Context context;
    private SharedPreferences mPrefs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        trakingService = (Switch) view.findViewById(R.id.settings_switch1);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        final SharedPreferences.Editor prefsEditor = mPrefs.edit();

        boolean hols = mPrefs.getBoolean("trakingService",false);

        if ( !mPrefs.contains("trakingService") || mPrefs.getBoolean("trakingService",false)){
            trakingService.setChecked(true);
        }else{
            trakingService.setChecked(false);
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
