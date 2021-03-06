package com.parkingdroid.parkingdroid.Fragments;



import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parkingdroid.parkingdroid.Fragments.FragmentIndoor;
import com.parkingdroid.parkingdroid.Fragments.FragmentMap;
import com.parkingdroid.parkingdroid.Fragments.Fragment_Parking;
import com.parkingdroid.parkingdroid.R;


public class MainActivityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainactivityfragment,container,false);

        final FragmentIndoor fragmentIndoor = new FragmentIndoor();
        final FragmentMap fragmentMap = new FragmentMap();
        final Fragment_Parking fragment_parking = new Fragment_Parking();


        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {

                switch (position){
                    case 0:
                        return fragment_parking ;

                    case 1:
                        return fragmentIndoor;

                    case 2:
                        return fragmentMap;

                    default:
                        return null;

                }

            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                switch (position){
                    case 0:
                        return getString(R.string.Tabbar_Parking);
                    case 1:
                        return getString(R.string.Tabbar_Indoor);
                    case 2:
                        return getString(R.string.Tabbar_Maps);
                    default:
                        return null;
                }
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
