package com.example.myapplication.model;

public class TransactionResponse2 {
    private boolean isSuccess;
    private String message;
    private Data data;

    // Getter và Setter
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Inner class cho data object
    public static class Data {
        private boolean isCompleteOrder;
        private int remainAmount;
        private int durationMinutes;

        private String maMayGiat;

        public String getMaMayGiat() {
            return maMayGiat == null? "": maMayGiat;
        }

        // Getter và Setter
        public boolean isCompleteOrder() {
            return isCompleteOrder;
        }

        public void setCompleteOrder(boolean completeOrder) {
            isCompleteOrder = completeOrder;
        }

        public int getRemainAmount() {
            return remainAmount;
        }

        public void setRemainAmount(int remainAmount) {
            this.remainAmount = remainAmount;
        }

        public int getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(int durationMinutes) {
            this.durationMinutes = durationMinutes;
        }
    }
}

