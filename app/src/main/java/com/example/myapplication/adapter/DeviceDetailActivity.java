package com.example.myapplication.adapter;

import static com.example.myapplication.api.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.example.myapplication.feature.createDevice.SelectServiceAdapter;
import com.example.myapplication.feature.createDevice.ServiceBottomSheet;
import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.Service;
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
    private AppCompatTextView tvAddService;
    RecyclerView rcv_data;
    private List<Service> listService;
    private SelectServiceAdapter selectServiceAdapter;
    CustomEditText edtDeviceType, edtServiceType, edtPercent;

    private Spinner spinnerAddress;
    private ArrayAdapter<String> addressAdapter;
    private String selectedAddress;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "QR Code Notifications";
            String description = "Notifications for saved QR Codes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("qr_code_channel", name, importance);
            channel.setDescription(description);

            // Đăng ký channel
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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
        rcv_data = findViewById(R.id.rcv_data);
        tvAddService = findViewById(R.id.tv_add_service);
        edtDeviceType = findViewById(R.id.edtDeviceType);
        edtServiceType = findViewById(R.id.edtServiceType);
        edtPercent = findViewById(R.id.edt_percent);
        spinnerAddress = findViewById(R.id.spinner_address);

        if (userData != null && userData.getType().equals("admin")) {
            edtOwner.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);
            tv_lb.setVisibility(View.VISIBLE);

            tvAddService.setVisibility(View.VISIBLE);
//            rcv_data.setVisibility(View.VISIBLE);
            edtPercent.setVisibility(View.VISIBLE);
            edtServiceType.setVisibility(View.VISIBLE);
            edtDeviceType.setVisibility(View.VISIBLE);
        }else {
            edtOwner.setVisibility(View.GONE);
            btn_update.setVisibility(View.GONE);
            tv_lb.setVisibility(View.GONE);

            tvAddService.setVisibility(View.GONE);
//            rcv_data.setVisibility(View.GONE);
            edtPercent.setVisibility(View.GONE);
            edtServiceType.setVisibility(View.GONE);
            edtDeviceType.setVisibility(View.GONE);
        }

        imgQR = findViewById(R.id.img_qr);
        btnDownQR = findViewById(R.id.btn_download_qr);

        device = (DeviceResponse.Device) getIntent().getSerializableExtra("DEVICE_DATA");
        if(device != null) {
            tvDeviceName.setText(device.getDeviceFullName());
            listService = device.getServices();
            edtDeviceType.setText(device.getDeviceType());
            edtServiceType.setText(device.getMachineType());
            if (device.getPercentAppDeducted() != null) {
                edtPercent.setText(String.valueOf(Math.round(device.getPercentAppDeducted() * 100)));
            }
        }

        boolean isAdmin = userData != null && "admin".equals(userData.getType());
        selectServiceAdapter = new SelectServiceAdapter(this, listService, isAdmin);
        rcv_data.setLayoutManager(new LinearLayoutManager(this));
        rcv_data.setAdapter(selectServiceAdapter);


        String deviceName = device.getDeviceName();
        String url = "https://iot.mimi.sg/?maMayGiat=" + deviceName;  // Chuỗi URL với tên máy giặt

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 512, 512);

            // Bước 2: Chuyển đổi BitMatrix thành Bitmap
            bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix .get(x, y) ? Color.BLACK : Color.WHITE);
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
                saveQRCodeToGallery2(bitmap);
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
            intent.putExtra("url",  BASE_URL + "/history?hostID="+device.getUserID()+"&deviceID=" + device.getDeviceId());
            intent.putExtra("header", "Lịch sử giặt");
            intent.putExtra("token", token);
            startActivity(intent);
        });
        tvReport.setOnClickListener(v -> {
            Intent intent = new Intent(DeviceDetailActivity.this, WebViewActivity.class);
            intent.putExtra("url", BASE_URL + "/report?hostID="+device.getUserID()+"&deviceID=" + device.getDeviceId());
            intent.putExtra("header", "Báo cáo/Thống kê");
            intent.putExtra("token", token);
            startActivity(intent);
        });
        addressAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_dropdown, new ArrayList<>());
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddress.setAdapter(addressAdapter);

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
                        updateSpinnerData();
                    }
                });

            }
        });

        updateSpinnerData();


        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddress = (String) parent.getItemAtPosition(position);
//                Toast.makeText(CreateDeviceActivity.this, "Đã chọn: " + selectedAddress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    updateUser();
                }
            }
        });

        tvAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listService.isEmpty()) {
                    Service service = listService.get(0);

                    ServiceBottomSheet serviceBottomSheet = new ServiceBottomSheet(service, listService);
                    serviceBottomSheet.show(getSupportFragmentManager(), serviceBottomSheet.getTag());
                    serviceBottomSheet.setOnSelected(new ServiceBottomSheet.OnTypeSelect() {
                        @Override
                        public void onChoose(List<Service> _listService) {
                            listService.addAll(_listService);
                            tvAddService.setVisibility(View.VISIBLE);
                            rcv_data.setVisibility(View.VISIBLE);
                            selectServiceAdapter.notifyDataSetChanged();
                        }
                    });
                }else {
                    ServiceBottomSheet serviceBottomSheet = new ServiceBottomSheet();
                    serviceBottomSheet.show(getSupportFragmentManager(), serviceBottomSheet.getTag());
                    serviceBottomSheet.setOnSelected(new ServiceBottomSheet.OnTypeSelect() {
                        @Override
                        public void onChoose(List<Service> _listService) {
                            listService.clear();
                            listService.addAll(_listService);
                            tvAddService.setVisibility(View.VISIBLE);
                            selectServiceAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void updateSpinnerData() {
        List<String> addresses = new ArrayList<>();

        if (userSelected == null) {
            addresses.add("Chọn địa chỉ"); // Hint ban đầu khi chưa chọn Owner
        } else {
            addresses.addAll(userSelected.getAddressNew());
            if(userSelected.getAddressNew().size() > 0) {
                selectedAddress = userSelected.getAddressNew().get(0);
            }
            if (addresses.isEmpty()) {
                addresses.add("Không có địa chỉ");
            }
        }

        addressAdapter.clear();
        addressAdapter.addAll(addresses);
        spinnerAddress.setSelection(0); // Luôn chọn item đầu tiên (hint hoặc địa chỉ đầu tiên)
        addressAdapter.notifyDataSetChanged();
    }

    private boolean validate() {
        if (edtDeviceType.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập loại thiết bị", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edtPercent.getText() == null || edtPercent.getText().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập % trích lại/giao dịch", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Float.parseFloat(edtPercent.getText().toString()) <= 0 || Float.parseFloat(edtPercent.getText().toString()) > 100) {
            Toast.makeText(this, "% trích lại/giao dịch phải trong khoản 0 -> 100", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtServiceType.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập dịch vụ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listService.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn dịch vụ", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if(selectedAddress == null|| selectedAddress.isEmpty()|| selectedAddress.equals("Chọn địa chỉ")) {
//            Toast.makeText(this, "Vui lòng chọn địa chỉ", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
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

    private void saveQRCodeToGallery2(Bitmap bitmap) {
        // Tạo tên cho ảnh
        String fileName = "QRCode_" + System.currentTimeMillis() + ".png";
        String savedImageURL = null;

        // Lưu ảnh vào thư viện
        try {
            savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    fileName,
                    "QR Code Image"
            );

            // Kiểm tra nếu ảnh đã được lưu
            if (savedImageURL != null) {
                // Thông báo ảnh đã được lưu thành công
                Toast.makeText(this, "QR máy giặt đã được tải về thư viện ảnh", Toast.LENGTH_SHORT).show();
                showSaveSuccessNotification(savedImageURL);
            } else {
                Toast.makeText(this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSaveSuccessNotification(String savedImageURL) {
        // Tạo Intent mở thư mục ảnh
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri imageUri = Uri.parse(savedImageURL);
        intent.setDataAndType(imageUri, "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Tạo PendingIntent để khi click vào thông báo sẽ mở thư mục ảnh
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "qr_code_channel")
                .setSmallIcon(R.drawable.ic_app) // Icon thông báo
                .setContentTitle("QR Code đã được lưu")
                .setContentText("QR Code đã được lưu vào thư viện ảnh")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // Đặt PendingIntent khi click vào thông báo
                .setAutoCancel(true); // Thông báo sẽ tự động biến mất khi người dùng click vào nó

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }


    public void updateEditedService(Service updatedService) {
        for (int i = 0; i < listService.size(); i++) {
            if (listService.get(i).get_id().equals(updatedService.get_id())) { // So sánh theo ID hoặc tiêu chí phù hợp
                listService.set(i, updatedService); // Cập nhật danh sách bên ngoài
                break;
            }
        }

        selectServiceAdapter.notifyDataSetChanged(); // Cập nhật giao diện

    }

    public void updateServiceList(Service removedService) {
        listService.remove(removedService); // Xóa item khỏi danh sách bên ngoài

        if (listService.isEmpty()) {
            rcv_data.setVisibility(View.GONE);
        }
        selectServiceAdapter.notifyDataSetChanged(); // Cập nhật giao diện
    }


    private void updateUser() {
        UpdateDeviceRequest request = new UpdateDeviceRequest();
        if(userSelected != null && userSelected.getId() != null) {
            request.setUserID(userSelected.getId());
        }
        if(listService != null) {
            request.setServices(listService);
        }
        float percent = Float.parseFloat(edtPercent.getText().toString()) / 100;
        request.setPercentAppDeducted(percent);

        if(edtDeviceType.getText() != null && !edtDeviceType.getText().isEmpty()) {
            request.setDeviceType(edtDeviceType.getText().toString());
        }
        if(edtServiceType.getText() != null && !edtServiceType.getText().isEmpty()) {
            request.setMachineType(edtServiceType.getText().toString());
        }
        if (selectedAddress != null && !selectedAddress.isEmpty() && !selectedAddress.equals("Chọn địa chỉ")) {
            request.setAddress(selectedAddress);
        }


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