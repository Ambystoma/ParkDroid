package com.parkingdroid.parkingdroid.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parkingdroid.parkingdroid.R;

/**
 * Created by hectorvintromarti on 02/06/16.
 */
public class FragmentHistoric extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_historic,container,false);

        return view;
    }
}
