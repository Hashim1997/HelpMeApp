package com.example.helpme.model;

/*
user order class to create order object that send it to firebase database and shown in helper home list consist of
Full name, location, car type, car color, email, helper id (helper email), description, price, state (if helper come), accept (price approve),
complete (if served), longitude and latitude for user location
with setter and getter
 */

public class UserOrder {

    private String fullName;
    private String location;
    private String carType;
    private String carColor;
    private String email;
    private String helperID;
    private String description;
    private String price;
    private boolean state;
    private String accept;
    private boolean complete;
    private double longitude;
    private double latitude;

    public UserOrder() {}

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getHelperID() {
        return helperID;
    }

    public void setHelperID(String helperID) {
        this.helperID = helperID;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }



    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
