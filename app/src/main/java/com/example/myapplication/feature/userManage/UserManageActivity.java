package com.example.myapplication.feature.userManage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.feature.createAccount.CreateAccountActivity;
import com.example.myapplication.feature.userDetail.UserDetailActivity;
import com.example.myapplication.model.ChangePasswordRequest;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.SimpleResult;
import com.example.myapplication.model.UpdateUserRequest;
import com.example.myapplication.model.UserResponse;
import com.example.myapplication.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManageActivity extends AppCompatActivity {

    RecyclerView rcvUser;
    ImageView ivBack, ivReload;
    AppCompatButton btnAddAccount;
    @SuppressLint("MissingInflatedId")

    String token = "";
    private ApiService apiService;
    private List<UserResponse.User> userList;
    private ManageUserAdapter manageUserAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_manage);
        getWindow().setStatusBarColor(Color.parseColor("#ff4b19"));
        rcvUser = findViewById(R.id.rcv_user);
        ivBack = findViewById(R.id.iv_back);
        btnAddAccount = findViewById(R.id.btn_addAccount);
        LoginResponse.Data userData = SharedPreferencesUtil.getUserData(this);
        if (userData != null) {
            token = userData.getToken();
        }

        userList = new ArrayList<>();

        manageUserAdapter = new ManageUserAdapter(this, userList);
        manageUserAdapter.setItemClick(new ManageUserAdapter.ItemClick() {
            @Override
            public void onClick(UserResponse.User user) {
                Intent intent = new Intent(UserManageActivity.this, UserDetailActivity.class);
                intent.putExtra("USER_DATA", user); // Truyền đối tượng user
                startActivity(intent);
            }

            @Override
            public void onEdit(UserResponse.User user) {
                EditUserDialogFragment dialogFragment = EditUserDialogFragment.newInstance(user);
                dialogFragment.setOnUserUpdatedListener(updatedUser -> {
                    // Xử lý logic cập nhật người dùng ở đây
                    updateUserInDatabase(updatedUser); // Hàm tự định nghĩa để cập nhật dữ liệu
                });
                dialogFragment.show(getSupportFragmentManager(), "EditUserDialogFragment");
            }

            @Override
            public void onChangePass(UserResponse.User user) {
                ChangePassUserDialogFragment dialogFragment = ChangePassUserDialogFragment.newInstance(user);
                dialogFragment.setOnUserUpdatedListener(newpass -> {
                    changePass(newpass, user.getId());
                });
                dialogFragment.show(getSupportFragmentManager(), "ChangePassUserDialogFragment");
            }
        });

        rcvUser.setLayoutManager(new LinearLayoutManager(this));
        rcvUser.setAdapter(manageUserAdapter);


        apiService = RetrofitClient.getInstance().create(ApiService.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManageActivity.this, CreateAccountActivity.class);
                startActivityForResult(intent, 999);
            }
        });

        getListUser();

    }

    private void updateUserInDatabase(UserResponse.User updatedUser) {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                updatedUser.getPhoneNumber(),                       // phoneNumber
                updatedUser.getFullName(),                     // fullName
                updatedUser.getAddress(), // address
                updatedUser.isActive()                               // isActive
        );

        Call<SimpleResult> call = apiService.updateUser("Bearer " + token, updatedUser.getId(), updateUserRequest);

        call.enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(@NonNull Call<SimpleResult> call, @NonNull Response<SimpleResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SimpleResult apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        getListUser();
                    }else {
                        Toast.makeText(UserManageActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserManageActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {
                Toast.makeText(UserManageActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePass(String newpass, String userId) {
        ChangePasswordRequest request = new ChangePasswordRequest(userId, newpass);


        Call<SimpleResult> call = apiService.changePassword("Bearer " + token, request);

        call.enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(@NonNull Call<SimpleResult> call, @NonNull Response<SimpleResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UserManageActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserManageActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {
                Toast.makeText(UserManageActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getListUser();
    }

    private void getListUser() {
        Call<UserResponse> call = apiService.getUsers("Bearer " + token);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        userList.addAll(apiResponse.getData());
                        manageUserAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(UserManageActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserManageActivity.this, "Danh sách người dùng trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(UserManageActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}