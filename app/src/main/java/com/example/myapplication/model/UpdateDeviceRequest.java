package com.example.myapplication.model;

public class UpdateDeviceRequest {
    private String userID;

    public UpdateDeviceRequest(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
