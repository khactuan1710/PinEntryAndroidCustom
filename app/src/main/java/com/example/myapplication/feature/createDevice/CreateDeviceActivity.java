package com.example.myapplication.feature.createDevice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CustomView.CustomEditText;
import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.feature.createAccount.CreateAccountActivity;
import com.example.myapplication.feature.createAccount.TypeAccountBottomSheet;
import com.example.myapplication.model.CreateDeviceRequest;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.Service;
import com.example.myapplication.model.SimpleResult;
import com.example.myapplication.model.UserResponse;
import com.example.myapplication.util.SharedPreferencesUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateDeviceActivity extends AppCompatActivity {

    CustomEditText edtOwner, edtDeviceName, edtDeviceFullName, edtIdDevice, edtDeviceType, edtMachineType;
    ImageView ivBack, ivReload;
    AppCompatButton btnAddDevice;

    String token = "";
    private ApiService apiService;
    private List<UserResponse.User> userList;
    UserResponse.User userSelected;

    RecyclerView rcv_data;

    TextView tvCount;
    LinearLayout lnlInfo, lnlQR;
    private SelectServiceAdapter selectServiceAdapter;
    private List<Service> listService;
    Bitmap bitmap;
    private AppCompatButton btnDownQR;
    private ImageView imgQR;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_device);
        getWindow().setStatusBarColor(Color.parseColor("#ff4b19"));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LoginResponse.Data userData = SharedPreferencesUtil.getUserData(this);
        if (userData != null) {
            token = userData.getToken();
        }
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        userList = new ArrayList<>();
        listService = new ArrayList<>();

        edtOwner = findViewById(R.id.tv_owner);
        edtDeviceName = findViewById(R.id.edtDeviceName);
        edtDeviceFullName = findViewById(R.id.edtDeviceFullName);
        edtIdDevice = findViewById(R.id.edtIdDevice);
        edtDeviceType = findViewById(R.id.edtDeviceType);
        edtMachineType = findViewById(R.id.edtMachineType);
        ivBack = findViewById(R.id.iv_back);
        ivReload = findViewById(R.id.iv_reload);
        btnAddDevice = findViewById(R.id.btn_addDevice);
        rcv_data = findViewById(R.id.rcv_data);
        tvCount = findViewById(R.id.tv_count);
        lnlInfo = findViewById(R.id.lnl_info);
        lnlQR = findViewById(R.id.lnl_qr);
        imgQR = findViewById(R.id.img_qr);
        btnDownQR = findViewById(R.id.btn_download_qr);


        selectServiceAdapter = new SelectServiceAdapter(this, listService);
        rcv_data.setLayoutManager(new LinearLayoutManager(this));
        rcv_data.setAdapter(selectServiceAdapter);


        getListUser();

        ivBack.setOnClickListener(view -> finish());
        btnAddDevice.setOnClickListener(view -> {
            if (validate()) {
//                Toast.makeText(this, "Thêm thiết bị thành công", Toast.LENGTH_SHORT).show();
//                finish();
                registerDevice();
            }
        });

        edtOwner.setOnClickViewMaskListener(new CustomEditText.OnClickViewMaskListener() {
            @Override
            public void onClickViewMask() {
                OwnerBottomSheet bottomSheet = new OwnerBottomSheet(CreateDeviceActivity.this, userList);
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setOnSelected(new OwnerBottomSheet.OnSelect() {
                    @Override
                    public void onChoose(UserResponse.User user) {
                        userSelected = user;
                        edtOwner.setText(user.getFullName());
                    }
                });

            }
        });

        edtMachineType.setOnClickViewMaskListener(new CustomEditText.OnClickViewMaskListener() {
            @Override
            public void onClickViewMask() {
                TypeServiceBottomSheet bottomSheet = new TypeServiceBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setOnSelected(new TypeServiceBottomSheet.OnTypeSelect() {
                    @Override
                    public void onChoose(String type) {
                        edtMachineType.setText(type);
                        switch (type) {
                            case "Máy giặt":
                                    ServiceBottomSheet serviceBottomSheet = new ServiceBottomSheet();
                                    serviceBottomSheet.show(getSupportFragmentManager(), serviceBottomSheet.getTag());
                                    serviceBottomSheet.setOnSelected(new ServiceBottomSheet.OnTypeSelect() {
                                        @Override
                                        public void onChoose(List<Service> _listService) {
                                            listService.clear();
                                            listService.addAll(_listService);
                                            tvCount.setText("Số dịch vụ đã thêm: " + _listService.size());
                                            selectServiceAdapter.notifyDataSetChanged();
                                        }
                                    });
                                break;
                            case "Thang máy":
//                                edtDeviceType.setText("Thang máy");
                                break;
                        }
                    }
                });
            }
        });

        btnDownQR.setOnClickListener(v -> {
            // Kiểm tra quyền lưu trữ
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveQRCodeToGallery(bitmap);
            } else {
                // Yêu cầu quyền nếu chưa có
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });

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
                    }else {
                        Toast.makeText(CreateDeviceActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CreateDeviceActivity.this, "Danh sách người dùng trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(CreateDeviceActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerDevice() {

        CreateDeviceRequest deviceRequest = new CreateDeviceRequest(
                userSelected.getId(),
                edtDeviceName.getText().toString(),
                edtDeviceFullName.getText().toString(),
                edtIdDevice.getText().toString(),
                edtDeviceType.getText().toString(),
                edtMachineType.getText().toString(),
                "",
                "",
                listService
        );

        Call<SimpleResult> call =  apiService.registerDevice("Bearer " + token, deviceRequest);
        call.enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(Call<SimpleResult> call, Response<SimpleResult> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateDeviceActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    lnlInfo.setVisibility(View.GONE);
                    lnlQR.setVisibility(View.VISIBLE);

                    initQR();
//                    finish();
                }else {
                    Toast.makeText(CreateDeviceActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initQR() {
        String url = "https://iot.mimi.sg/?maMayGiat=" + edtDeviceName.getText();  // Chuỗi URL với tên máy giặt

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 512, 512);

            // Bước 2: Chuyển đổi BitMatrix thành Bitmap
            bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Bước 3: Hiển thị mã QR lên ImageView
            imgQR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void saveQRCodeToGallery(Bitmap bitmap) {
        // Tạo tên cho ảnh
        String fileName = "QRCode_" + System.currentTimeMillis() + ".png";

        // Lưu ảnh vào bộ nhớ
        try {
            // Lưu ảnh vào thư viện
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    fileName,
                    "QR Code Image"
            );

            // Kiểm tra nếu ảnh đã được lưu
            if (savedImageURL != null) {
                Toast.makeText(this, "QR máy giặt đã được tải về thư viện ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save QR Code", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        if (edtOwner.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập chủ thiết bị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtDeviceName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên thiết bị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtDeviceFullName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đầy đủ thiết bị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtIdDevice.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã thiết bị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtDeviceType.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập loại thiết bị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtMachineType.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn dịch vụ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listService.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn dịch vụ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}