package com.example.myapplication.feature.createAccount;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

    CustomEditText edtUsername, edtPassword, edtPhoneNumber, edtFullname, edtAddress, edtPercent , edtWeLinkAcc, edtWeLinkPass, edtBankCode, edtSTK, edtSTKName, typeAccount;
    ApiService apiService;
    ImageView ivBack, ivReload;
    AppCompatButton btnAddAccount;
    private List<BankCodeResponse.BankCode> bankCodeList = new ArrayList<>();
    private String token;
    BankCodeResponse.BankCode bankCodeSelected;

    @SuppressLint("MissingInflatedId")
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
        ivBack = findViewById(R.id.iv_back);
        edtPercent = findViewById(R.id.edt_percent);

        edtWeLinkAcc.setText("khachuong.vn@gmail.com");
        edtWeLinkPass.setText("devup@2023");

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        getBankCodes();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                        edtBankCode.setText(bankCode.getShortName());
                        bankCodeSelected = bankCode;
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
        String bankCode = "";
        if(bankCodeSelected != null) {
            bankCode = bankCodeSelected.getBin();
        }
        float percent = Float.parseFloat(edtPercent.getText().toString()) / 100; // Chuyển sang float và chia 100

        RegisterRequest request = new RegisterRequest(
                edtPhoneNumber.getText().toString(),
                edtPhoneNumber.getText().toString(),
                edtPassword.getText().toString(),
                edtFullname.getText().toString(),
                edtAddress.getText().toString(),
                percent,
                edtWeLinkAcc.getText().toString(),
                edtWeLinkPass.getText().toString(),
                bankCode,
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
        if(edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }else if (edtPassword.getText().toString().trim().length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        };

        if(edtPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtFullname.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(Float.parseFloat(edtPercent.getText().toString()) <= 0 || Float.parseFloat(edtPercent.getText().toString()) > 100) {
            Toast.makeText(this, "% trích lại/giao dịch phải trong khoản 0 -> 100", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtWeLinkAcc.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tài khoản weLink", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtWeLinkPass.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu weLink", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtBankCode.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngân hàng", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtSTK.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tài khoản", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtSTKName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên tài khoản", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(typeAccount.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn loại tài khoản", Toast.LENGTH_SHORT).show();
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