package com.example.myapplication.feature.createAccount;

import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.feature.userManage.UserManageActivity;
import com.example.myapplication.model.BankCodeResponse;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.SimpleResult;
import com.example.myapplication.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    CustomEditText edtUsername, edtPassword, edtPhoneNumber, edtFullname, edtAddress, edtWeLinkAcc, edtWeLinkPass, edtBankCode, edtSTK, edtSTKName, typeAccount;
    ApiService apiService;
    AppCompatButton btnAddAccount;
    private List<BankCodeResponse.BankCode> bankCodeList = new ArrayList<>();
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        getWindow().setStatusBarColor(Color.parseColor("#ff4b19"));
        LoginResponse.Data userData = SharedPreferencesUtil.getUserData(this);
        if (userData != null) {
            token = userData.getToken();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtFullname = findViewById(R.id.edtFullname);
        edtAddress = findViewById(R.id.edtAddress);
        edtWeLinkAcc = findViewById(R.id.edtWeLinkAcc);
        edtWeLinkPass = findViewById(R.id.edtWeLinkPass);
        edtBankCode = findViewById(R.id.bankCode);
        edtSTK = findViewById(R.id.edtSTK);
        edtSTKName = findViewById(R.id.edtSTKName);
        typeAccount = findViewById(R.id.typeAccount);
        btnAddAccount = findViewById(R.id.btn_addAccount);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        getBankCodes();

        typeAccount.setOnClickViewMaskListener(new CustomEditText.OnClickViewMaskListener() {
            @Override
            public void onClickViewMask() {
                TypeAccountBottomSheet bottomSheet = new TypeAccountBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setOnAddressSelected(new TypeAccountBottomSheet.OnTypeSelect() {
                    @Override
                    public void onChoose(String type) {
                        typeAccount.setText(type);
                    }
                });
            }
        });
        edtBankCode.setOnClickViewMaskListener(new CustomEditText.OnClickViewMaskListener() {
            @Override
            public void onClickViewMask() {
                BankCodeBottomSheet bottomSheet = new BankCodeBottomSheet(CreateAccountActivity.this, bankCodeList);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setOnBankCodeSelected(new BankCodeBottomSheet.OnBankCodeSelect() {
                    @Override
                    public void onChoose(BankCodeResponse.BankCode bankCode) {
                        edtBankCode.setText(bankCode.getName());
                    }
                });
            }
        });

        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                   registerAccount();
                }
            }
        });

    }

    private void registerAccount() {
        RegisterRequest request = new RegisterRequest(
                edtUsername.getText().toString(),
                edtPhoneNumber.getText().toString(),
                edtPassword.getText().toString(),
                edtFullname.getText().toString(),
                edtAddress.getText().toString(),
                edtWeLinkAcc.getText().toString(),
                edtWeLinkPass.getText().toString(),
                edtBankCode.getText().toString(),
                edtSTK.getText().toString(),
                edtSTKName.getText().toString(),
                typeAccount.getText().toString().equals("Admin")? "admin" : "host"
        );

        Call<SimpleResult> call = apiService.register("Bearer " + token, request);
        call.enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(Call<SimpleResult> call, Response<SimpleResult> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateAccountActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(CreateAccountActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private boolean validate() {
        if(edtUsername.getText().toString().isEmpty()) {
            edtUsername.setError("Vui lòng nhập tài khoản");
            return false;
        }
        if(edtPassword.getText().toString().isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }

        if(edtPhoneNumber.getText().toString().isEmpty()) {
            edtPhoneNumber.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if(edtFullname.getText().toString().isEmpty()) {
            edtFullname.setError("Vui lòng nhập họ tên");
            return false;
        }

        if(edtAddress.getText().toString().isEmpty()) {
            edtAddress.setError("Vui lòng nhập địa chỉ");
            return false;
        }
        if(edtWeLinkAcc.getText().toString().isEmpty()) {
            edtWeLinkAcc.setError("Vui lòng nhập tài khoản weLink");
            return false;
        }

        if(edtWeLinkPass.getText().toString().isEmpty()) {
            edtWeLinkPass.setError("Vui lòng nhập mật khẩu weLink");
            return false;
        }
        if(edtBankCode.getText().toString().isEmpty()) {
            edtBankCode.setError("Vui lòng chọn ngân hàng");
            return false;
        }

        if(edtSTK.getText().toString().isEmpty()) {
            edtSTK.setError("Vui lòng nhập số tài khoản");
            return false;
        }

        if(edtSTKName.getText().toString().isEmpty()) {
            edtSTKName.setError("Vui lòng nhập tên tài khoản");
            return false;
        }
        if(typeAccount.getText().toString().isEmpty()) {
            typeAccount.setError("Vui lòng chọn loại tài khoản");
            return false;
        };

        return true;
    }

    private void getBankCodes() {
        Call<BankCodeResponse> call = apiService.getBankCodes();

        call.enqueue(new Callback<BankCodeResponse>() {
            @Override
            public void onResponse(@NonNull Call<BankCodeResponse> call, @NonNull Response<BankCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BankCodeResponse apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        bankCodeList = apiResponse.getData();
                    }else {
                        Toast.makeText(CreateAccountActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BankCodeResponse> call, Throwable t) {
                Toast.makeText(CreateAccountActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}