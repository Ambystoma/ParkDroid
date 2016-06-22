package com.parkingdroid.parkingdroid.Models;

public class Gactivity {

    private int cotxe;
    private int bicy;
    private int corrent;
    private int inclinat; //TILTING
    private int walking;
    private int onfoot;
    private int still;
    private int noze; //UNKNOWN


    public int getCotxe() {
        return cotxe;
    }

    public void setCotxe(int cotxe) {
        this.cotxe = cotxe;
    }

    public int getBicy() {
        return bicy;
    }

    public void setBicy(int bicy) {
        this.bicy = bicy;
    }

    public int getCorrent() {
        return corrent;
    }

    public void setCorrent(int corrent) {
        this.corrent = corrent;
    }

    public int getInclinat() {
        return inclinat;
    }

    public void setInclinat(int inclinat) {
        this.inclinat = inclinat;
    }

    public int getWalking() {
        return walking;
    }

    public void setWalking(int walking) {
        this.walking = walking;
    }

    public int getOnfoot() {
        return onfoot;
    }

    public void setOnfoot(int onfoot) {
        this.onfoot = onfoot;
    }

    public int getStill() {
        return still;
    }

    public void setStill(int still) {
        this.still = still;
    }

    public int getNoze() {
        return noze;
    }

    public void setNoze(int noze) {
        this.noze = noze;
    }
}
