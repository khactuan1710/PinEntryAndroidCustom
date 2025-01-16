package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.DeviceAdapter;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.login.LoginActivity;
import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.model.TransactionInfo;
import com.example.myapplication.webview.WebViewActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
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

    AppCompatTextView tvFullName;
    RecyclerView recyclerView;
    String token = "";
    String fullname = "";
    private List<DeviceResponse.Device> deviceList;
    private DeviceAdapter deviceAdapter;
    private ApiService apiService;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        button = findViewById(R.id.btn);

        Activity activity = this;
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        token = sharedPreferences.getString("AUTH_TOKEN", null);
        fullname = sharedPreferences.getString("FULL_NAME", null);
        tvFullName = findViewById(R.id.tv_full_name);

        tvFullName.setText("Xin chào " + (fullname == null ? "" : fullname));

        AppCompatButton btnHistory = findViewById(R.id.btn_history);

        ImageView ivHotline = findViewById(R.id.iv_hotline);
        ivHotline.setOnClickListener(view -> {
            String phoneNumber = "0983322285";

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không tìm thấy ứng dụng gọi điện", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView ivLogout = findViewById(R.id.iv_logout);
        ivLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        // Xóa token trong SharedPreferences
                        SharedPreferences sharedPreferences2 = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences2.edit();
                        editor.putString("AUTH_TOKEN", null);
                        editor.apply();

                        // Điều hướng sang màn hình login
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Đóng Activity hiện tại
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss(); // Đóng dialog nếu nhấn "Hủy"
                    })
                    .show();
        });


        deviceList = new ArrayList<>();
        recyclerView = findViewById(R.id.rcv_list);
        deviceAdapter = new DeviceAdapter(this, deviceList);
        deviceAdapter.setItemClick(new DeviceAdapter.ItemClick() {
            @Override
            public void onClick(String deviceId, int isOnOff) {
                callAPIOnOff(isOnOff, deviceId, apiService);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(deviceAdapter);

        callGetListDevice();


//        AppCenter.start(getApplication(), "f078b81b-441d-4735-a936-3c5275f5ec17",
//                Analytics.class, Crashes.class, Distribute.class);


        //startForegroundService
        Intent intent22 = new Intent(activity, SocketService.class);
        activity.startService(intent22);





        btnHistory.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://iot.mimi.sg/history");
            intent.putExtra("header", "Lịch sử giặt");
            intent.putExtra("token", token);
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

//        Switch switchWasher001 = findViewById(R.id.switch_washer_001);
//        Switch switchWasher002 = findViewById(R.id.switch_washer_002);
//
//        // Sự kiện cho Máy giặt 001
//        switchWasher001.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
////                Toast.makeText(this, "Máy giặt 001 được bật", Toast.LENGTH_SHORT).show();
////                onOff(1, "m001");
//                // Xử lý logic khi bật Máy giặt 001
//                callAPIOnOff(1, "m001", apiService);
//            } else {
////                Toast.makeText(this, "Máy giặt 001 được tắt", Toast.LENGTH_SHORT).show();
////                onOff(0, "m001");
//                // Xử lý logic khi tắt Máy giặt 001
//                callAPIOnOff(0, "m001", apiService);
//            }
//        });
//
//        // Sự kiện cho Máy giặt 002
//        switchWasher002.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
////                Toast.makeText(this, "Máy giặt 002 được bật", Toast.LENGTH_SHORT).show();
////                onOff(1, "m002");
//                // Xử lý logic khi bật Máy giặt 002
//                callAPIOnOff(1, "m002", apiService);
//            } else {
////                Toast.makeText(this, "Máy giặt 002 được tắt", Toast.LENGTH_SHORT).show();
////                onOff(0, "m002");
//                // Xử lý logic khi tắt Máy giặt 002
//                callAPIOnOff(0, "m002", apiService);
//            }
//        });

        startSmsRetriever();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

    }

    private void callGetListDevice() {

        Call<DeviceResponse> call = apiService.getDevices("Bearer " + token);

        call.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeviceResponse> call, @NonNull Response<DeviceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeviceResponse apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        deviceList.addAll(apiResponse.getData());
                        deviceAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(MainActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Danh sách thiết bị trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        Call<ApiResponse> call = apiService.toggleDevice("Bearer " + token, deviceRequest);

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
                    Toast.makeText(MainActivity.this, "thất bại!", Toast.LENGTH_SHORT).show();
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