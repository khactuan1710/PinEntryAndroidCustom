package com.example.myapplication.model;

import java.util.List;

public class UpdateUserRequest {
    private String phoneNumber;
    private String fullName;
    private String address;
    private boolean isActive;
    private List<String> addressNew;
    private String bankCode;
    private String bankAccountNumber;
    private String bankAccountName;

    public UpdateUserRequest(String phoneNumber, String fullName, String address, boolean isActive, List<String> addressNew, String bankCode, String bankAccountNumber, String bankAccountName) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.address = address;
        this.isActive = isActive;
        this.addressNew = addressNew;
        this.bankCode = bankCode;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountName = bankAccountName;
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

