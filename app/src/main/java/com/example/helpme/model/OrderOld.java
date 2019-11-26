package com.example.helpme.model;

public class OrderOld {
    private String HelperName;
    private int orderRate;
    private String HelperExp;

    public OrderOld() {
    }

    public String getHelperName() {
        return HelperName;
    }

    public void setHelperName(String helperName) {
        HelperName = helperName;
    }

    public int getOrderRate() {
        return orderRate;
    }

    public void setOrderRate(int orderRate) {
        this.orderRate = orderRate;
    }

    public String getHelperExp() {
        return HelperExp;
    }

    public void setHelperExp(String helperExp) {
        HelperExp = helperExp;
    }
}
