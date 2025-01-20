package com.example.myapplication.feature.userDetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.DeviceAdapter;
import com.example.myapplication.adapter.DeviceDetailActivity;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.feature.userManage.UserManageActivity;
import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.UserResponse;
import com.example.myapplication.util.SharedPreferencesUtil;
import com.example.myapplication.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    private List<DeviceResponse.Device> deviceList;
    private DeviceAdapter deviceAdapter;
    private ApiService apiService;
    String token = "";
    RecyclerView recyclerView;
    private UserResponse.User user;
    AppCompatTextView tvUsername, tvPhoneNumber, tvStatus ,tvAddress;
    AppCompatButton btnHistory, btnReport;
    ImageView ivBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.rcv_list);
        tvUsername = findViewById(R.id.tv_username);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvStatus = findViewById(R.id.tv_status_active);
        tvAddress = findViewById(R.id.tv_address);
        btnHistory = findViewById(R.id.btn_history);
        btnReport = findViewById(R.id.btn_report);
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        getWindow().setStatusBarColor(Color.parseColor("#ff4b19"));
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        LoginResponse.Data userData = SharedPreferencesUtil.getUserData(this);
        if (userData != null) {
            token = userData.getToken();
        }

        deviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(this, token, deviceList);
        deviceAdapter.setItemClick(new DeviceAdapter.ItemClick() {
            @Override
            public void onClick(String deviceId, int isOnOff) {
                callAPIOnOff(isOnOff, deviceId, apiService);
            }

            @Override
            public void onDetail(DeviceResponse.Device device) {
                    Intent intent = new Intent(UserDetailActivity.this, DeviceDetailActivity.class);
                    intent.putExtra("DEVICE_DATA", device); // Truyền đối tượng user
                    startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(deviceAdapter);

        user = (UserResponse.User) getIntent().getSerializableExtra("USER_DATA");

        if (user != null) {
            tvUsername.setText(user.getFullName());
            tvPhoneNumber.setText(user.getPhoneNumber());
            if(user.isActive()) {
                tvStatus.setText("Hoạt động");
                tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                tvStatus.setText("Đang khoá");
                tvStatus.setTextColor(Color.parseColor("#ff4b19"));
            }
            tvAddress.setText(user.getAddress());
        }

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDetailActivity.this, WebViewActivity.class);
                intent.putExtra("url", "https://iot.mimi.sg/history?hostID=" + user.getId());
                intent.putExtra("header", "Lịch sử giặt");
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDetailActivity.this, WebViewActivity.class);
                intent.putExtra("url", "https://iot.mimi.sg/report?hostID=" + user.getId());
                intent.putExtra("header", "Báo cáo/thống kê");
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
        callGetListDevice();
    }

    private void callGetListDevice() {

        Call<DeviceResponse> call = apiService.getDevicesByHost("Bearer " + token, user.getId());

        call.enqueue(new Callback<DeviceResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeviceResponse> call, @NonNull Response<DeviceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeviceResponse apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        deviceList.addAll(apiResponse.getData());
                        deviceAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(UserDetailActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserDetailActivity.this, "Danh sách thiết bị trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeviceResponse> call, Throwable t) {
                Toast.makeText(UserDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                        Toast.makeText(UserDetailActivity.this, "Lỗi: " + apiResponse.getError(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserDetailActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserDetailActivity.this, "thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}