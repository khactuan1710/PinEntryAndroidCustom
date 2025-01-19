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
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.util.SharedPreferencesUtil;

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
}