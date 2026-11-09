package com.example.myapplication.feature.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.ConfigResponse;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.util.SharedPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        fetchPublicConfig();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Kiểm tra token trong SharedPreferences
            String token = "";
            LoginResponse.Data userData = SharedPreferencesUtil.getUserData(this);
            if (userData != null) {
                token = userData.getToken();
            }

            Intent intent;
            if (token != null && !token.isEmpty()) {
                // Nếu token tồn tại, chuyển sang màn hình Home
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Nếu không có token, chuyển sang màn hình Login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish(); // Đóng màn Splash
        }, 1000);
    }

    /**
     * Hỏi server xem QR phải in theo URL nào, rồi lưu lại để các màn sinh QR dùng.
     * Chạy nền và cố tình KHÔNG chặn luồng vào app: thất bại thì vẫn dùng giá trị cũ
     * đã lưu, hoặc địa chỉ server mặc định.
     */
    private void fetchPublicConfig() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getConfig().enqueue(new Callback<ConfigResponse>() {
            @Override
            public void onResponse(Call<ConfigResponse> call, Response<ConfigResponse> response) {
                ConfigResponse body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess()
                        && body.getData() != null) {
                    SharedPreferencesUtil.saveQrBaseUrl(
                            getApplicationContext(), body.getData().getQrBaseUrl());
                }
            }

            @Override
            public void onFailure(Call<ConfigResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}