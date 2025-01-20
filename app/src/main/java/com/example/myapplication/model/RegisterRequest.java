package com.example.myapplication.model;

public class RegisterRequest {
    private final String username;
    private final String phoneNumber;
    private final String password;
    private final String fullName;
    private final String address;
    private final String eWeLinkAccount;
    private final String eWeLinkPassword;
    private final String bankCode;
    private final String bankAccountNumber;
    private final String bankAccountName;
    private final String type;
    private final float percentAppDeducted;

    public RegisterRequest(String username, String phoneNumber, String password, String fullName, String address,
                           float percentAppDeducted, String eWeLinkAccount, String eWeLinkPassword, String bankCode,
                           String bankAccountNumber, String bankAccountName, String type) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.percentAppDeducted = percentAppDeducted;
        this.eWeLinkAccount = eWeLinkAccount;
        this.eWeLinkPassword = eWeLinkPassword;
        this.bankCode = bankCode;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountName = bankAccountName;
        this.type = type;
    }
}
