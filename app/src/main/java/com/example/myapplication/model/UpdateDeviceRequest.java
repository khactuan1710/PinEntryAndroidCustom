package com.example.myapplication.model;

import java.util.List;

public class UpdateDeviceRequest {
    private String userID;
    private List<Service> services; // Thay đổi kiểu dữ liệu thành List<Service

    private String deviceType;
    private String machineType;
    private float percentAppDeducted;
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public UpdateDeviceRequest(String userID) {
        this.userID = userID;
    }
    public void setPercentAppDeducted(float percentAppDeducted) {
        this.percentAppDeducted = percentAppDeducted;
    }
    public UpdateDeviceRequest() {
    }

    public List<Service> getServices() {
        return services;
    }
    public void setServices(List<Service> services) {
        this.services = services;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
