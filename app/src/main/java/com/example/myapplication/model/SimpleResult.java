package com.example.myapplication.model;

public class SimpleResult {
    private boolean isSuccess;
    private String message;

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message == null ? "Lỗi không xác định" : message;
    }
}
