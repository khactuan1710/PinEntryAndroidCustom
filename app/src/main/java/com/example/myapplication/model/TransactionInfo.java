package com.example.myapplication.model;

public class TransactionInfo {
    private String amount;
    private String transactionCode;

    private String orderID;


    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    private String smsFull;

    public String getSmsFull() {
        return smsFull;
    }



    public void setSmsFull(String smsFull) {
        this.smsFull = smsFull;
    }

    public TransactionInfo(String amount, String transactionCode) {
        this.amount = amount;
        this.transactionCode = transactionCode;
    }

    public TransactionInfo(String amount, String transactionCode, String orderId) {
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.orderID = orderId;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getTransactionCode() {
        return transactionCode == null ? "" : transactionCode;
    }
}
