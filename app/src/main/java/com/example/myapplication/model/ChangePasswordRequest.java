package com.example.myapplication.model;

public class ChangePasswordRequest {
    private String obj_id;
    private String newPassword;

    public ChangePasswordRequest(String obj_id, String newPassword) {
        this.obj_id = obj_id;
        this.newPassword = newPassword;
    }

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}