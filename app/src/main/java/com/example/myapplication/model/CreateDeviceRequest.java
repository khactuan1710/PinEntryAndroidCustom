package com.example.myapplication.model;

import java.util.List;

public class CreateDeviceRequest {

    private String accountID;
    private String deviceName;
    private String deviceFullName;
    private String deviceID;
    private String deviceType;
    private String machineType;
    private String latitude;
    private String longitude;
    private List<Service> services;

    private float percentAppDeducted;

    public CreateDeviceRequest(String accountID, String deviceName, String deviceFullName, String deviceID,
                         String deviceType, String machineType, String latitude, String longitude, float percentAppDeducted,  List<Service> services) {
        this.accountID = accountID;
        this.deviceName = deviceName;
        this.deviceFullName = deviceFullName;
        this.deviceID = deviceID;
        this.deviceType = deviceType;
        this.machineType = machineType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.percentAppDeducted = percentAppDeducted;
        this.services = services;
    }

}
