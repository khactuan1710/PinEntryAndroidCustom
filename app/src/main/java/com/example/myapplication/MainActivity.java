package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.TransactionInfo;
import com.example.myapplication.webview.WebViewActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

//    PinEntryEditText pinEntryEditText;
//    Button button;

    private  String selectedUrl = "";

    private SmsBroadcastReceiver smsBroadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        button = findViewById(R.id.btn);
        Activity activity = this;
        Spinner spinnerUrl = findViewById(R.id.spinner_url);
        AppCompatButton btnEnter = findViewById(R.id.btn_enter);
        AppCompatButton btnHistory = findViewById(R.id.btn_history);

        //startForegroundService
        Intent intent22 = new Intent(activity, SocketService.class);
        activity.startService(intent22);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);




        btnHistory.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://iot.mimi.sg/history");
            intent.putExtra("header", "Lịch sử giặt");
            startActivity(intent);
        });
//        pinEntryEditText = findViewById(R.id.txt_pin_entry);
//        Intent intent = new Intent(activity, SocketService.class);
//        activity.startService(intent);

        String[] urls = {
                "https://iot.mimi.sg",
                "http://192.168.1.11:5001",
                "http://192.168.1.15:5001",
                "http://192.168.1.208:5001",
                "http://192.168.1.12:5001"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, urls);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUrl.setAdapter(adapter);

        btnEnter.setOnClickListener(view -> {
            selectedUrl = spinnerUrl.getSelectedItem().toString();
            if (!selectedUrl.isEmpty()) {
                // Gửi URL mới đến Service
                Intent intent2 = new Intent(this, SocketService.class);
                intent2.putExtra("socket_url", selectedUrl);
                startService(intent2);
            } else {
                Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            }
        });

        Switch switchWasher001 = findViewById(R.id.switch_washer_001);
        Switch switchWasher002 = findViewById(R.id.switch_washer_002);

        // Sự kiện cho Máy giặt 001
        switchWasher001.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                Toast.makeText(this, "Máy giặt 001 được bật", Toast.LENGTH_SHORT).show();
//                onOff(1, "m001");
                // Xử lý logic khi bật Máy giặt 001
                callAPIOnOff(1, "m001", apiService);
            } else {
//                Toast.makeText(this, "Máy giặt 001 được tắt", Toast.LENGTH_SHORT).show();
//                onOff(0, "m001");
                // Xử lý logic khi tắt Máy giặt 001
                callAPIOnOff(0, "m001", apiService);
            }
        });

        // Sự kiện cho Máy giặt 002
        switchWasher002.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                Toast.makeText(this, "Máy giặt 002 được bật", Toast.LENGTH_SHORT).show();
//                onOff(1, "m002");
                // Xử lý logic khi bật Máy giặt 002
                callAPIOnOff(1, "m002", apiService);
            } else {
//                Toast.makeText(this, "Máy giặt 002 được tắt", Toast.LENGTH_SHORT).show();
//                onOff(0, "m002");
                // Xử lý logic khi tắt Máy giặt 002
                callAPIOnOff(0, "m002", apiService);
            }
        });

        startSmsRetriever();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

    }

    private void onOff(int isOnOff, String deviceId) {
        Intent intent2 = new Intent(this, SocketService.class);
        intent2.putExtra("isOnOff", isOnOff);
        intent2.putExtra("deviceId", deviceId);
        startService(intent2);
    }

    public TransactionInfo parseMessage(String message) {
        // Biểu thức chính quy để tìm số tiền và mã giao dịch
        String regex = "\\+([0-9,.]+)\\sVND.*ND:\\s(.*)";

        // Compile biểu thức chính quy
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        // Kiểm tra và lấy kết quả
        if (matcher.find()) {
            String amount = matcher.group(1);  // Số tiền
            String transactionCode = matcher.group(2);  // Mã giao dịch

            // Trả về đối tượng TransactionInfo chứa số tiền và mã giao dịch
            return new TransactionInfo(amount, transactionCode);
        } else {
            // Xử lý nếu không tìm thấy dữ liệu
//            Toast.makeText(getApplicationContext(), "Không tìm thấy thông tin giao dịch.", Toast.LENGTH_SHORT).show();
            return null;  // Trả về null nếu không tìm thấy thông tin
        }
    }

    private void callAPIOnOff(int isOnOff, String deviceId, ApiService apiService) {
        DeviceRequest deviceRequest = new DeviceRequest(deviceId, isOnOff);

        Call<ApiResponse> call = apiService.toggleDevice(deviceRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getError() != null) {
                        Toast.makeText(MainActivity.this, "Lỗi: " + apiResponse.getError(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
//                    Toast.makeText(MainActivity.this, "Gọi API thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startSmsRetriever() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(aVoid -> {
//            Toast.makeText(this, "SMS Retriever started!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
//            Toast.makeText(this, "Failed to start SMS Retriever!", Toast.LENGTH_SHORT).show();
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