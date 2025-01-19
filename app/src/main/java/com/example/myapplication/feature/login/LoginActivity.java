package com.example.myapplication.feature.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    CustomEditText edUsername;
    CustomEditText edPassword;
    AppCompatButton loginBtn;
    ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);
        edUsername = findViewById(R.id.edtPhoneNumber);
        edPassword = findViewById(R.id.edtPassword);
        loginBtn = findViewById(R.id.btn_login);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();
                // Xử lý đăng nhập ở đây
                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập tài khoản", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(username, password);

                Call<LoginResponse> call = apiService.login(loginRequest);

                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse apiResponse = response.body();
                           if(apiResponse.isSuccess()) {
                               Toast.makeText(LoginActivity.this,
                                       apiResponse.getMessage(),
                                       Toast.LENGTH_LONG).show();
                               SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                               SharedPreferences.Editor editor = sharedPreferences.edit();
                               editor.putString("AUTH_TOKEN", apiResponse.getData().getToken());
                               editor.putString("FULL_NAME", apiResponse.getData().getFullName());
                               editor.apply(); // Lưu thay đổi

                               // Điều hướng sang màn hình Home
                               Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                               startActivity(intent);
                               finish();
                           }else {
                               Toast.makeText(LoginActivity.this,
                                       apiResponse.getMessage(),
                                       Toast.LENGTH_LONG).show();
                           }
                        } else {
                            Toast.makeText(LoginActivity.this, "Thông tin tài khoản không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}