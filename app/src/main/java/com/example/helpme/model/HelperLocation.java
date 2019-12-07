package com.example.helpme.model;

/*
helper location class to create location object that deal with live location in background services and sent to firebase consist of
longitude, latitude and key
with setter and getter
 */
public class HelperLocation {

    private Double longitude;
    private Double Latitude;
    private String key;

    public HelperLocation() {
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
