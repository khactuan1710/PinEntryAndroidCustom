package com.example.myapplication.model;

public class TransactionRequest {
    private String orderID;
    private int amount;
    private String smsContent;
    private String smsFull;
    private String sender;
    private String deviceId; // ID của thiết bị

    public void setSender(String sender) {
        this.sender = sender;
    }

    public TransactionRequest(String orderID, int amount, String smsContent) {
        this.orderID = orderID;
        this.amount = amount;
        this.smsContent = smsContent;
    }

    public TransactionRequest() {
    }

    public String getSmsFull() {
        return smsFull;
    }

    public void setSmsFull(String smsFull) {
        this.smsFull = smsFull;
    }

    // Getter và Setter (nếu cần)
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

