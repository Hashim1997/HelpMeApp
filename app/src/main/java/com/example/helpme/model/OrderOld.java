package com.example.helpme.model;

/*
Old Order class to create order object that save old order in firebase database that completed consist of
helper name, order rate and helper experience
with setter and getter
 */

public class OrderOld {
    private String HelperName;
    private float orderRate;
    private String HelperExp;

    public OrderOld() {
    }

    public String getHelperName() {
        return HelperName;
    }

    public void setHelperName(String helperName) {
        HelperName = helperName;
    }

    public float getOrderRate() {
        return orderRate;
    }

    public void setOrderRate(float orderRate) {
        this.orderRate = orderRate;
    }

    public String getHelperExp() {
        return HelperExp;
    }

    public void setHelperExp(String helperExp) {
        HelperExp = helperExp;
    }
}
