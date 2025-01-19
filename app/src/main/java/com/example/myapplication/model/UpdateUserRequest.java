package com.example.myapplication.model;

public class UpdateUserRequest {
    private String phoneNumber;
    private String fullName;
    private String address;
    private boolean isActive;

    public UpdateUserRequest(String phoneNumber, String fullName, String address, boolean isActive) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.address = address;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

