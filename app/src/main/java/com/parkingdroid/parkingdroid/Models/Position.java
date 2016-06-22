package com.parkingdroid.parkingdroid.Models;


import java.util.Date;

public class Position {

    private Double latitude;
    private Double longitude;
    private String street;
    private Date date;

    public Position(Double lat, Double lon, String strit, Date data){
        this.setLatitude(lat);
        this.setLongitude(lon);
        this.setStreet(strit);
        this.setDate(data);
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
