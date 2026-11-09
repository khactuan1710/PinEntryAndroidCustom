package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Phản hồi của GET /config — cấu hình công khai server trả về cho app. */
public class ConfigResponse implements Serializable {
    @SerializedName("isSuccess")
    private boolean isSuccess;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message == null ? "Lỗi không xác định" : message;
    }

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable {
        /** URL dùng để in QR dán trên máy, dạng <qrBaseUrl>/?maMayGiat=<mã máy>. */
        @SerializedName("qrBaseUrl")
        private String qrBaseUrl;

        public String getQrBaseUrl() {
            return qrBaseUrl;
        }
    }
}
