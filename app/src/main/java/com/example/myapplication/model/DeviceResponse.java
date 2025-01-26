package com.example.myapplication.model;

import java.io.Serializable;
import java.util.ArrayList;
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

    public static class Device implements Serializable {
        private String deviceID;
        private String currentStatus;
        private String deviceName;
        private String deviceFullName;
        private String userID;
        private String deviceType;
        private String machineType;
        private Float percentAppDeducted;
        private List<Service> services;

        public String getUserID() {
            return userID == null ? "" : userID;
        }

        public String getDeviceId() {
            return deviceID == null ? "" : deviceID;
        }

        public String getDeviceFullName() {
            return deviceFullName == null ? "" : deviceFullName;
        }

        public List<Service> getServices() {
            return services == null ? new ArrayList<>() : services;
        }

        public String getMachineType() {
            return machineType == null ? "" : machineType;
        }

        public String getDeviceType() {
            return deviceType == null ? "" : deviceType;
        }

        public String getDeviceID() {
            return deviceID == null ? "" : deviceID;
        }

        public Float getPercentAppDeducted() {

            return percentAppDeducted;

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