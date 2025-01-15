package com.example.myapplication.model;

public class TransactionInfo {
    private String amount;
    private String transactionCode;

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

    public String getAmount() {
        return amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }
}
