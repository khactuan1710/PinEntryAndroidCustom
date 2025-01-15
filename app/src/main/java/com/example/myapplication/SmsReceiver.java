package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.myapplication.api.ApiService2;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

//                        Toast.makeText(context, "SMS từ: " + sender + "\nNội dung: " + messageBody, Toast.LENGTH_LONG).show();
//                        Log.e("f", "SMS từ: " + sender + "\nNội dung: "+ messageBody);

                        Intent serviceIntent = new Intent(context, SocketService.class);
                        serviceIntent.putExtra("sender", sender);
                        serviceIntent.putExtra("message", messageBody);
                        context.startService(serviceIntent);

//                        Intent broadcastIntent = new Intent("com.example.SMS_RECEIVED");
//                        broadcastIntent.putExtra("sender", sender);
//                        broadcastIntent.putExtra("message", messageBody);
//                        context.sendBroadcast(broadcastIntent);

//                        Intent serviceIntent = new Intent(context, ApiService2.class);
//                        serviceIntent.putExtra("sender", sender);
//                        serviceIntent.putExtra("message", messageBody);
//                        context.startService(serviceIntent);
//                        showToast(context, "bbbbb.");

                    }
                }
            }
        }
    }
    private void showToast(final Context context, final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
