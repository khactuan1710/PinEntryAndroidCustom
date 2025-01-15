package com.example.myapplication.api;

import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.TransactionInfo;
import com.example.myapplication.model.TransactionRequest;
import com.example.myapplication.model.TransactionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiService2 extends IntentService {

    public ApiService2() {
        super("ApiService2");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // Lấy dữ liệu từ intent
            String sender = intent.getStringExtra("sender");
            String message = intent.getStringExtra("message");

            if (message == null || sender == null) {
                Log.e("ApiService2", "Sender or message is null");
                return;
            }

            // Khởi tạo Retrofit
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

            TransactionInfo transactionInfo = parseMessage(message);

            // Tạo đối tượng TransactionRequest
            TransactionRequest transactionRequest = new TransactionRequest(
                    "osijdihidhhd",
                    Integer.parseInt(transactionInfo.getAmount().replace(".", "")),
                    transactionInfo.getTransactionCode()
            );

            Call<TransactionResponse> call = apiService.createTransaction(transactionRequest);

            try {
                // Thực hiện gọi API đồng bộ trong IntentService
                Response<TransactionResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    TransactionResponse transactionResponse = response.body();
                    Log.d("ApiService2", "Transaction success: " + transactionResponse.getMessage());

                    // Gửi thông báo về UI thread
                    showToast("Transaction success: " + transactionResponse.getMessage());
                } else {
                    Log.e("ApiService2", "Transaction failed: " + response.errorBody().string());
                    showToast("Transaction failed: " + response.errorBody().string());
                }
            } catch (Exception e) {
                Log.e("ApiService2", "Transaction API error: " + e.getMessage(), e);
                showToast("Transaction API error: " + e.getMessage());
            }
        }
    }
    // Sử dụng Handler để hiển thị Toast trong UI thread
    private void showToast(final String message) {
        Handler handler = new Handler(getApplicationContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public TransactionInfo parseMessage(String message) {
        // Biểu thức chính quy để tìm số tiền và mã giao dịch
        String regex = "\\+([0-9,.]+)\\sVND.*ND:\\s(.*)";

        // Compile biểu thức chính quy
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        // Kiểm tra và lấy kết quả
        if (matcher.find()) {
            String amount = matcher.group(1);  // Số tiền
            String transactionCode = matcher.group(2);  // Mã giao dịch

            // Trả về đối tượng TransactionInfo chứa số tiền và mã giao dịch
            return new TransactionInfo(amount, transactionCode);
        } else {
            // Xử lý nếu không tìm thấy dữ liệu
//            Toast.makeText(getApplicationContext(), "Không tìm thấy thông tin giao dịch.", Toast.LENGTH_SHORT).show();
            return null;  // Trả về null nếu không tìm thấy thông tin
        }
    }
}

