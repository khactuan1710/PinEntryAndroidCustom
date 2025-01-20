package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CustomView.CustomEditText;
import android.Manifest;
import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.feature.createDevice.CreateDeviceActivity;
import com.example.myapplication.feature.createDevice.OwnerBottomSheet;
import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.SimpleResult;
import com.example.myapplication.model.UpdateDeviceRequest;
import com.example.myapplication.model.UserResponse;
import com.example.myapplication.util.SharedPreferencesUtil;
import com.example.myapplication.webview.WebViewActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceDetailActivity extends AppCompatActivity {

    TextView tvDeviceName, tvHistory, tvReport;
    private DeviceAdapter deviceAdapter;
    private ApiService apiService;
    String token = "";
    private DeviceResponse.Device device;
    private List<UserResponse.User> userList;
    CustomEditText edtOwner;
    ImageView ivBack;
    private AppCompatButton btn_update, btnDownQR;
    private ImageView imgQR;
    TextView tv_lb;
    Bitmap bitmap;

    UserResponse.User userSelected;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        getWindow().setStatusBarColor(Color.parseColor("#ff4b19"));
        LoginResponse.Data userData = SharedPreferencesUtil.getUserData(this);
        if (userData != null) {
            token = userData.getToken();
        }
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        userList = new ArrayList<>();
        tvDeviceName = findViewById(R.id.tv_device_name);
        tvHistory = findViewById(R.id.tv_history);
        tvReport = findViewById(R.id.tv_report);
        edtOwner = findViewById(R.id.tv_owner);
        btn_update = findViewById(R.id.btn_update);
        tv_lb = findViewById(R.id.tv_lb);

        if (userData != null && userData.getType().equals("admin")) {
            edtOwner.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);
            tv_lb.setVisibility(View.VISIBLE);
        }else {
            edtOwner.setVisibility(View.GONE);
            btn_update.setVisibility(View.GONE);
            tv_lb.setVisibility(View.GONE);
        }

        imgQR = findViewById(R.id.img_qr);
        btnDownQR = findViewById(R.id.btn_download_qr);

        device = (DeviceResponse.Device) getIntent().getSerializableExtra("DEVICE_DATA");
        if(device != null) {
            tvDeviceName.setText(device.getDeviceFullName());
        }

        String deviceName = device.getDeviceName();
        String url = "https://iot.mimi.sg/?maMayGiat=" + deviceName;  // Chuỗi URL với tên máy giặt

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

        btnDownQR.setOnClickListener(v -> {
            // Kiểm tra quyền lưu trữ
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveQRCodeToGallery(bitmap);
            } else {
                // Yêu cầu quyền nếu chưa có
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });





        getListUser();

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });


        tvHistory.setPaintFlags(tvHistory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvReport.setPaintFlags(tvHistory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DeviceDetailActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://iot.mimi.sg/history?hostID="+device.getUserID()+"&deviceID=" + device.getDeviceId());
            intent.putExtra("header", "Lịch sử giặt");
            intent.putExtra("token", token);
            startActivity(intent);
        });
        tvReport.setOnClickListener(v -> {
            Intent intent = new Intent(DeviceDetailActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://iot.mimi.sg/report?hostID="+device.getUserID()+"&deviceID=" + device.getDeviceId());
            intent.putExtra("header", "Báo cáo/Thống kê");
            intent.putExtra("token", token);
            startActivity(intent);
        });

        edtOwner.setOnClickViewMaskListener(new CustomEditText.OnClickViewMaskListener() {
            @Override
            public void onClickViewMask() {
                OwnerBottomSheet bottomSheet = new OwnerBottomSheet(DeviceDetailActivity.this, userList);
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

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtOwner.getText() != null && !edtOwner.getText().isEmpty()) {
                    updateUser();
                }
            }
        });
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


    private void updateUser() {
        UpdateDeviceRequest request = new UpdateDeviceRequest(userSelected.getId());

        Call<SimpleResult> call = apiService.updateDevice("Bearer " +token, device.getDeviceId(), request);
        call.enqueue(new Callback<SimpleResult>() {
            @Override
            public void onResponse(Call<SimpleResult> call, Response<SimpleResult> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DeviceDetailActivity.this, "Cập nhật thành công:", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeviceDetailActivity.this, "Cập nhật thất bại:", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResult> call, Throwable t) {
                Toast.makeText(DeviceDetailActivity.this, "Lỗi khi gọi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DeviceDetailActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DeviceDetailActivity.this, "Danh sách người dùng trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(DeviceDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}