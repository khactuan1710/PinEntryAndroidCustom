package com.example.myapplication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserResponse {
    private boolean isSuccess;
    private String message;
    private List<User> data;

    // Getters and Setters
    public boolean isSuccess() {
        return isSuccess;
    }
    public String getMessage() {
        return message;
    }

    public List<User> getData() {
        return data;
    }

    public class User implements Serializable {
        private String _id;
        private String address;
        private String fullName;
        private boolean isActive;
        private String phoneNumber;
        private String type;
        private String username;
        private List<String> addressNew;
        private String bankCode;
        private String bankAccountNumber;
        private String bankAccountName;

        public String getBankCode() {
            return bankCode == null? "": bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getBankAccountNumber() {
            return bankAccountNumber ==null? "": bankAccountNumber;
        }

        public void setBankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

        public String getBankAccountName() {
            return bankAccountName == null? "": bankAccountName;
        }

        public void setBankAccountName(String bankAccountName) {
            this.bankAccountName = bankAccountName;
        }

        public List<String> getAddressNew() {
            return addressNew == null? new ArrayList<>(): addressNew;
        }

        public void setAddressNew(List<String> addressNew) {
            this.addressNew = addressNew;
        }

        // Getters and Setters
        public String getId() {
            return _id == null? "": _id;
        }

        public String getAddress() {
            return address == null ? "" : address;
        }
        public String getFullName() {
            return fullName == null ? "" : fullName;
        }
        public boolean isActive() {
            return isActive;
        }
        public String getPhoneNumber() {
            return phoneNumber == null ? "" : phoneNumber;
        }
        public String getType() {
            return type;
        }
        public String getUsername() {
            return username == null ? "" : username;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
