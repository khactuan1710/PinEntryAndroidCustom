package com.example.myapplication.model;

public class DeviceRequest {
    private String deviceId;
    private int isOnOff;
    private int duration; // số phút bật

    public DeviceRequest(String deviceId, int isOnOff, int duration) {
        this.deviceId = deviceId;
        this.isOnOff = isOnOff;
        this.duration = duration;
    }

    // Getter, Setter nếu cần
}
