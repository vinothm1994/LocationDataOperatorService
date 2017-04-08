package com.example.vinoth.locationoperator.model;

/**
 * Created by vinoth on 9/10/16.
 */

public class LocationDatas {

    private String speed;


    private double latitude;
    private double longitude;
    private  String timeStamp;

    public LocationDatas(double latitude, double longitude, String timeStamp,String speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.speed=speed;
    }

    public LocationDatas() {
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
