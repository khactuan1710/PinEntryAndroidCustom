package com.example.myapplication.model;

public class ApiResponse {
    private String device_name;
    private String device_id;
    private String result_on;
    private String result_off;
    private String error;
    private boolean isSuccess;
    private String message;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    // Getter và Setter
    public String getDeviceName() {
        return device_name;
    }

    public String getDeviceId() {
        return device_id;
    }

    public String getResultOn() {
        return result_on;
    }

    public String getResultOff() {
        return result_off;
    }

    public String getError() {
        return error;
    }
}
