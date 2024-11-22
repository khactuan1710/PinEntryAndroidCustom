package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    PinEntryEditText pinEntryEditText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);
        Activity activity = this;
        pinEntryEditText = findViewById(R.id.txt_pin_entry);
        Intent intent = new Intent(activity, SocketService.class);
        activity.startService(intent);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
//                } else {
//                    sendSms("0343938411", "Mã OTP của bạn là: 123456");
//                }


                Intent intent = new Intent(activity, CallActivity.class);
                startActivity(intent);


//                pinEntryEditText.clearFocus();
//
//
//
//                Integer i = 100;
//                int b = 100;
//                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + i.equals(b), Toast.LENGTH_SHORT).show();
//                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + pinEntryEditText.getText().length(), Toast.LENGTH_SHORT).show();
            }
        });
        pinEntryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();

                // Hiển thị Toast với độ dài hiện tại
                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + length, Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void sendSms(String phoneNumber, String message) {
        try {
            // Get the SmsManager system service
            SmsManager smsManager = SmsManager.getDefault();
            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "SMS sending failed!", Toast.LENGTH_SHORT).show();
        }
    }
}