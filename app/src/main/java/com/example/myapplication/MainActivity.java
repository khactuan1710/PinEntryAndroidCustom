package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

//    PinEntryEditText pinEntryEditText;
//    Button button;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        button = findViewById(R.id.btn);
        Activity activity = this;
        AppCompatEditText edtUrl = findViewById(R.id.edt);
        AppCompatButton btnEnter = findViewById(R.id.btn_enter);
//        pinEntryEditText = findViewById(R.id.txt_pin_entry);
        Intent intent = new Intent(activity, SocketService.class);
        activity.startService(intent);

        btnEnter.setOnClickListener(view -> {
            String url = edtUrl.getText().toString().trim();
            if (!url.isEmpty()) {
                // Gửi URL mới đến Service
                Intent intent2 = new Intent(this, SocketService.class);
                intent2.putExtra("socket_url", url);
                startService(intent2);
                Toast.makeText(this, "Socket URL updated to: " + url, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            }
        });

        startSmsRetriever();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
//                }
////                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
////                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
////                } else {
////                    sendSms("0343938411", "Mã OTP của bạn là: 123456");
////                }
//
//
////                Intent intent = new Intent(activity, CallActivity.class);
////                startActivity(intent);
//
//
////                pinEntryEditText.clearFocus();
////
////
////
////                Integer i = 100;
////                int b = 100;
////                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + i.equals(b), Toast.LENGTH_SHORT).show();
////                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + pinEntryEditText.getText().length(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        pinEntryEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                int length = editable.length();
//
//                // Hiển thị Toast với độ dài hiện tại
//                Toast.makeText(pinEntryEditText.getContext(), "Độ dài hiện tại: " + length, Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    private void startSmsRetriever() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "SMS Retriever started!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to start SMS Retriever!", Toast.LENGTH_SHORT).show();
        });

        // Đăng ký BroadcastReceiver để nhận tin nhắn
        smsBroadcastReceiver = new SmsBroadcastReceiver(message -> {
            Log.d("SMS", "Tin nhắn nhận được: " + message);

            // Trích xuất thông tin từ tin nhắn (nếu là OTP)
            if (message.contains("OTP:")) {
//                String otp = extractOtp(message);
                Log.d("SMS", "OTP nhận được: " + message);

                // Gửi thông tin đến SocketService
                Intent serviceIntent = new Intent(this, SocketService.class);
                serviceIntent.putExtra("otp", message);
                startService(serviceIntent);
            } else {
                Log.e("SMS", "Không tìm thấy OTP trong tin nhắn");
            }
        });

        IntentFilter filter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsBroadcastReceiver != null) {
            unregisterReceiver(smsBroadcastReceiver);
        }
    }

    // Hàm trích xuất OTP từ tin nhắn
    private String extractOtp(String message) {
        // Ví dụ: Tin nhắn có dạng "Your OTP is: 123456"
        String otp = message.replaceAll("[^0-9]", ""); // Lấy toàn bộ số trong tin nhắn
        return otp.length() > 6 ? otp.substring(0, 6) : otp; // Giả định OTP là 6 chữ số
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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

//    public void sendSms(String phoneNumber, String message) {
//        try {
//            Intent intent = new Intent(Intent.ACTION_SENDTO);
//            intent.setData(Uri.parse("smsto:" + phoneNumber)); // Thay đổi thành "smsto:" để gửi SMS
//            intent.putExtra("sms_body", message);
//            startActivity(intent); // Mở ứng dụng nhắn tin mặc định để gửi tin
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "SMS sending failed!", Toast.LENGTH_SHORT).show();
//        }
//    }

}