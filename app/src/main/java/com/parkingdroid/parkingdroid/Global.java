package com.parkingdroid.parkingdroid;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.parkingdroid.parkingdroid.Constants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Global extends Application {

    private List <String> adresses;
    private ArrayList <String> dates;
    private List<String> latlongs;

    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private Set<String> set;
    private boolean isLogued;
    private boolean isGeofence;



    public Global(){


    }

    public Global (Context context) {
        super();
        adresses = new ArrayList<>();
        dates = new ArrayList<>();
        mPref = context.getSharedPreferences(Constants.SP_ADRESS,MODE_PRIVATE);
        editor = mPref.edit();
        set = new HashSet<>();
        latlongs = new ArrayList<>();
        isGeofence=true;



        if (mPref.contains(Constants.SP_ADRESS_LIST)){

            this.set = mPref.getStringSet(Constants.SP_ADRESS_LIST, new HashSet<String>());
            this.adresses.addAll(set);
            this.set.clear();
            this.set = mPref.getStringSet(Constants.SP_ADRESS_DATE_LIST,new HashSet<String>());
            this.dates.addAll(set);
            this.set.clear();
            this.set = mPref.getStringSet(Constants.SP_LATLONG_LIST, new HashSet<String>());
            this.latlongs.addAll(set);
            this.set.clear();

        }
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates.add(dates);
        set= new HashSet<>(this.dates);
        editor.putStringSet(Constants.SP_ADRESS_DATE_LIST,set);
        set = null;
        editor.apply();
    }

    public List<String> getAdresses(){
        return  adresses;
    }

    public void setAdresses (String adresses){
        this.adresses.add(adresses);
        set = new HashSet<>(this.adresses);
        editor.putStringSet(Constants.SP_ADRESS_LIST,set);
        set = null;
        editor.apply();
    }

    public List<String> getLatlongs(){
        return  latlongs;
    }

    public void setLatlongs (String latlongs){
        this.latlongs.add(latlongs);
        set = new HashSet<>(this.latlongs);
        editor.putStringSet(Constants.SP_LATLONG_LIST,set);
        set = null;
        editor.apply();
    }


    public boolean isLogued() {
        return isLogued;
    }

    public void setLogued(boolean logued) {

        isLogued = logued;
        editor.putBoolean(Constants.SP_ISLOGED,logued);
        editor.commit();
    }

    public boolean isGeofence() {
        return isGeofence;
    }

    public void setGeofence(boolean geofence) {
        isGeofence = geofence;
    }


}

