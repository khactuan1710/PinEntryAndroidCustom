package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Service implements Serializable {

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("price")
    private int price;

    @SerializedName("totalMinutes")
    private int totalMinutes;

    // Constructor
    public Service(String serviceName, int price, int totalMinutes) {
        this.serviceName = serviceName;
        this.price = price;
        this.totalMinutes = totalMinutes;
    }

    // Getters
    public String getServiceName() {
        return serviceName == null ? "" : serviceName;
    }

    public int getPrice() {
        return price;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", price=" + price +
                ", totalMinutes=" + totalMinutes +
                '}';
    }
}
