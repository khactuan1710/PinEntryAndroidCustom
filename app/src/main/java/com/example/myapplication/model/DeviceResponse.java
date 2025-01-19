package com.example.myapplication.model;

import java.util.List;

public class DeviceResponse {
    private List<Device> data;
    private boolean isSuccess;
    private String message;


    public List<Device> getData() {
        return data == null ? null : data;
    }

    public void setData(List<Device> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Device {
        private String deviceID;
        private String currentStatus;
        private String deviceName;
        private String deviceFullName;
        private String userID;

        public String getUserID() {
            return userID == null ? "" : userID;
        }

        public String getDeviceId() {
            return deviceID == null ? "" : deviceID;
        }

        public String getDeviceFullName() {
            return deviceFullName == null ? "" : deviceFullName;
        }

        public void setDeviceId(String deviceId) {
            this.deviceID = deviceId;
        }

        public String getCurrentStatus() {
            return currentStatus == null ? "" : currentStatus;
        }

        public void setCurrentStatus(String currentStatus) {
            this.currentStatus = currentStatus;
        }

        public String getDeviceName() {
            return deviceName == null ? "" : deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }
    }
}