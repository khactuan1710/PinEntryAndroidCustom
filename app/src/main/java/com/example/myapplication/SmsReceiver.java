package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

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
                    }
                }
            }
        }
    }
}
