package com.example.myapplication;

import static com.example.myapplication.api.RetrofitClient.BASE_URL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.adapter.DeviceAdapter;
import com.example.myapplication.adapter.DeviceDetailActivity;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.feature.createAccount.CreateAccountActivity;
import com.example.myapplication.feature.createDevice.CreateDeviceActivity;
import com.example.myapplication.feature.login.LoginActivity;
import com.example.myapplication.feature.userDetail.UserDetailActivity;
import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.BankCodeResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.TransactionInfo;
import com.example.myapplication.feature.userManage.UserManageActivity;
import com.example.myapplication.util.SharedPreferencesUtil;
import com.example.myapplication.webview.WebViewActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

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
    private DeviceStatusReceiver deviceStatusReceiver;

    AppCompatTextView tvFullName;
    RecyclerView recyclerView;

    AppCompatButton buttonUserManage, buttonAddDevice, btnReport, btnReportAdmin;
    String token = "";
    String fullname = "";

    private List<DeviceResponse.Device> deviceList;
    private List<DeviceResponse.Device> originalDeviceList;
    private DeviceAdapter deviceAdapter;
    private ApiService apiService;
    private CustomEditText edtSearch, edtMinus;

    LoginResponse.Data userData;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        button = findViewById(R.id.btn);
        getWindow().setStatusBarColor(Color.parseColor("#ff4b19"));
        Activity activity = this;
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        userData = SharedPreferencesUtil.getUserData(this);
        if (userData != null) {
            token = userData.getToken();
            fullname = userData.getFullName();
        }
        tvFullName = findViewById(R.id.tv_full_name);
        buttonUserManage = findViewById(R.id.btn_user_manage);
        buttonAddDevice = findViewById(R.id.btn_addDevice);
        edtSearch = findViewById(R.id.edt_search);
        btnReport = findViewById(R.id.btn_report);
        btnReportAdmin = findViewById(R.id.btn_report_admin);
        edtMinus = findViewById(R.id.edt_default_minus);
        int defaultMinutes = SharedPreferencesUtil.getDefaultMinutes(this);
        edtMinus.setText(String.valueOf(defaultMinutes));
        edtMinus.setOnTextChangeListener(s -> {
            try {
                int newMinutes = Integer.parseInt(s.trim());
                SharedPreferencesUtil.saveDefaultMinutes(this, newMinutes);
            } catch (NumberFormatException e) {
            }
        });


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);
//                    Toast.makeText(MainActivity.this, "FCM Token: " + token, Toast.LENGTH_LONG).show();
                });


        buttonAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateDeviceActivity.class);
                startActivityForResult(intent, 888);
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", BASE_URL + "/report?hostID=" + userData.getUserID());
                intent.putExtra("header", "Báo cáo/thống kê");
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        btnReportAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", BASE_URL + "/admin/report?hostID=" + userData.getUserID());
                intent.putExtra("header", "Báo cáo/thống kê");
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });


        edtSearch.setOnTextChangeListener(new CustomEditText.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                if (text != null && !text.isEmpty()) {
                    // Lọc danh sách dựa trên shortName
                    List<DeviceResponse.Device> filteredList = new ArrayList<>();
                    for (DeviceResponse.Device device : originalDeviceList) {
                        if (device.getDeviceFullName() != null &&
                                device.getDeviceFullName().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add(device);
                        }
                    }

                    // Cập nhật lại danh sách và thông báo cho adapter
                    deviceList.clear();
                    deviceList.addAll(filteredList);
                    deviceAdapter.notifyDataSetChanged();
                }else {
                    // Nếu không có văn bản tìm kiếm, hiển thị danh sách ban đầu
                    deviceList.clear();
                    deviceList.addAll(originalDeviceList);
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        });

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
                        SharedPreferencesUtil.clearUserData(MainActivity.this);
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
        deviceAdapter = new DeviceAdapter(this, token, deviceList);
        deviceAdapter.setItemClick(new DeviceAdapter.ItemClick() {
            @Override
            public void onClick(String deviceId, int isOnOff) {
                callAPIOnOff(isOnOff, deviceId, apiService);
            }

            @Override
            public void onDetail(DeviceResponse.Device device) {
                    Intent intent = new Intent(MainActivity.this, DeviceDetailActivity.class);
                    intent.putExtra("DEVICE_DATA", device); // Truyền đối tượng user
                    startActivityForResult(intent, 777);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(deviceAdapter);

        callGetListDevice();

        Intent intent22 = new Intent(activity, SocketService.class);
        activity.startService(intent22);

        if (userData != null && !userData.getType().equals("admin")) {
            buttonUserManage.setVisibility(View.GONE);
            buttonAddDevice.setVisibility(View.GONE);
            btnReport.setVisibility(View.VISIBLE);

        }else if(userData == null) {
            buttonUserManage.setVisibility(View.GONE);
            buttonAddDevice.setVisibility(View.GONE);
            btnReport.setVisibility(View.GONE);
        }

        if (userData.getType().equals("admin")) {
            btnReportAdmin.setVisibility(View.VISIBLE);
        } else {
            btnReportAdmin.setVisibility(View.GONE);
        }
        buttonUserManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserManageActivity.class);
                startActivity(intent);
            }
        });



        btnHistory.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", BASE_URL + "/history");
            intent.putExtra("header", "Lịch sử giặt");
            intent.putExtra("token", token);
            startActivity(intent);
        });


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
                        deviceList.clear();
                        deviceList.addAll(apiResponse.getData());
                        originalDeviceList = new ArrayList<>(deviceList);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callGetListDevice();
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
        DeviceRequest deviceRequest = new DeviceRequest(deviceId, isOnOff, getDefaultMinutesFromEditText());

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

    public int getDefaultMinutesFromEditText() {
        String input = edtMinus.getText().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
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
        
        // Đăng ký BroadcastReceiver cho cập nhật trạng thái thiết bị
        deviceStatusReceiver = new DeviceStatusReceiver();
        IntentFilter deviceStatusFilter = new IntentFilter(SocketService.DEVICE_STATUS_ACTION);
        registerReceiver(deviceStatusReceiver, deviceStatusFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsBroadcastReceiver != null) {
            unregisterReceiver(smsBroadcastReceiver);
        }
        if (deviceStatusReceiver != null) {
            unregisterReceiver(deviceStatusReceiver);
        }
    }

    // Hàm trích xuất OTP từ tin nhắn
    private String extractOtp(String message) {
        // Ví dụ: Tin nhắn có dạng "Your OTP is: 123456"
        String otp = message.replaceAll("[^0-9]", ""); // Lấy toàn bộ số trong tin nhắn
        return otp.length() > 6 ? otp.substring(0, 6) : otp; // Giả định OTP là 6 chữ số
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//            } else {
////                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


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

    /**
     * BroadcastReceiver để nhận thông tin cập nhật trạng thái thiết bị từ SocketService
     */
    private class DeviceStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(SocketService.DEVICE_STATUS_ACTION)) {
                String deviceId = intent.getStringExtra("deviceId");
                boolean isOn = intent.getBooleanExtra("isOn", false);
                int durationMinutes = intent.getIntExtra("durationMinutes", 0);
                int minutesRemaining = intent.getIntExtra("minutesRemaining", 0);
                
                Log.d("MainActivity", "Received device update: DeviceId=" + deviceId + 
                        ", isOn=" + isOn + ", duration=" + durationMinutes + 
                        ", remaining=" + minutesRemaining);
                
                // Tìm thiết bị trong danh sách và cập nhật trạng thái
                if (deviceId != null && !deviceId.isEmpty()) {
                    // Kiểm tra xem các thiết bị có trong danh sách không
                    if (deviceList != null) {
                        Log.d("MainActivity", "Total devices in list: " + deviceList.size());
                        // Thử cả hai cách để tìm thiết bị
                        boolean found = false;
                        for (DeviceResponse.Device device : deviceList) {
                            // In ra ID của thiết bị theo nhiều cách khác nhau
                            Log.d("MainActivity", "Device ID comparison - API: " + deviceId +
                                   ", List deviceID: " + device.getDeviceID() +
                                   ", List deviceId: " + device.getDeviceId());

                            // Thử cả hai cách
                            if (device.getDeviceId().equals(deviceId) ||
                                device.getDeviceID().equals(deviceId)) {
                                found = true;
                                break;
                            }
                        }
                        Log.d("MainActivity", "Device found in list: " + found);
                        if (found) {
                            updateDeviceStatus(deviceId, isOn);
                        } else {
                            Log.w("MainActivity", "⚠️ Device not found in list, skip update: " + deviceId);
                        }
                    }
                } else {
                    Log.e("MainActivity", "Received null or empty deviceId");
                }
            }
        }
    }
    
    /**
     * Cập nhật trạng thái của thiết bị trong danh sách
     * @param deviceId ID của thiết bị cần cập nhật
     * @param isOn Trạng thái bật/tắt mới
     */
    private void updateDeviceStatus(String deviceId, boolean isOn) {
        if (deviceList != null && deviceAdapter != null) {
            for (int i = 0; i < deviceList.size(); i++) {
                DeviceResponse.Device device = deviceList.get(i);
                if (device.getDeviceId().equals(deviceId)) {
                    // Cập nhật trạng thái trong model
                    device.setCurrentStatus(isOn ? "on" : "off");
                    
                    // Thông báo adapter để cập nhật UI
                    deviceAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }
}