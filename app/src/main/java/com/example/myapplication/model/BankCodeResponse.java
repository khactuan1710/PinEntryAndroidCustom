package com.example.myapplication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankCodeResponse implements Serializable{
    private List<BankCode> data;
    private boolean isSuccess;
    private String message;

    public List<BankCode> getData() {
        return data == null ? new ArrayList<>() : data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public static class BankCode implements Serializable {
        private String bin;
        private String code;
        private int isTransfer;
        private String logo;
        private int lookupSupported;
        private String name;
        private String shortName;
        private String short_name;
        private int support;
        private String swift_code;
        private int transferSupported;

        public String getBin() {
            return bin == null ? "" : bin;
        }

        public String getCode() {
            return code == null ? "" : code;
        }

        public int getIsTransfer() {
            return isTransfer;
        }

        public String getLogo() {
            return logo == null ? "" : logo;
        }

        public int getLookupSupported() {
            return lookupSupported;
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public String getShortName() {
            return shortName == null ? "" : shortName;
        }

        public String getShort_name() {
            return short_name == null ? "" : short_name;
        }

        public int getSupport() {
            return support;
        }

        public String getSwift_code() {
            return swift_code;
        }

        public int getTransferSupported() {
            return transferSupported;
        }
    }
}