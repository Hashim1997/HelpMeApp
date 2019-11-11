package com.example.helpme.model;

public class Helper {

    private String fullName;
    private String password;
    private String email;
    private String phoneNum;
    private String levelOfExperience;
    private String typeOfExperience;
    private String location;
    private double longitude;
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Helper(){}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getLevelOfExperience() {
        return levelOfExperience;
    }

    public void setLevelOfExperience(String levelOfExperience) {
        this.levelOfExperience = levelOfExperience;
    }

    public String getTypeOfExperience() {
        return typeOfExperience;
    }

    public void setTypeOfExperience(String typeOfExperience) {
        this.typeOfExperience = typeOfExperience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
