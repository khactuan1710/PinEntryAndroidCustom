package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.auth.api.phone.SmsRetriever;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private SmsListener smsListener;

    public SmsBroadcastReceiver(SmsListener listener) {
        this.smsListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Tin nhắn SMS nhận được thành công
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (smsListener != null) {
                        smsListener.onMessageReceived(message);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Timeout khi không nhận được SMS
                    Log.e("SmsBroadcastReceiver", "Timeout: Không nhận được tin nhắn");
                    break;
            }
        }
    }

    public interface SmsListener {
        void onMessageReceived(String message);
    }
}
