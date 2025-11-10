package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import android.os.AsyncTask;
import retrofit2.Call;
import retrofit2.Response;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.TransactionInfo;
import com.example.myapplication.model.TransactionRequest;
import com.example.myapplication.model.TransactionResponse;
import com.example.myapplication.model.TransactionResponse2;
import com.example.myapplication.util.SharedPreferencesUtil;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Response;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketService extends Service {

    private Socket mSocket;
    public static final String CHANNEL_ID = "socket_channel_id";
    public static final String DEVICE_STATUS_ACTION = "com.example.myapplication.DEVICE_STATUS_UPDATE";
    
    // Map để lưu trữ CountDownTimer cho từng thiết bị
    private HashMap<String, CountDownTimer> deviceTimers = new HashMap<>();
    private String socketUrl = "http://192.168.1.5:8080";

    @Override
    public void onCreate() {
        super.onCreate();

        // Tạo channel thông báo cho ứng dụng
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Socket Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Context context = this;
        // Tạo và bắt đầu Foreground Service
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Quản lý máy giặt")
                .setContentText("Quản lý mắy giặt của tôi")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Đổi thành icon thực tế trong ứng dụng của bạn
                .build();

        startForeground(1, notification);

        // Kết nối tới server
//        connectSocket(socketUrl);


    }


    private void connectSocket(String url) {
        if (mSocket != null) {
            // Ngắt kết nối
            if (mSocket.connected()) {
                mSocket.disconnect();
            }
            // Xóa tất cả listener để tránh lỗi callback cũ bị gọi lại
            mSocket.off();
            mSocket = null;
        }

        try {
            mSocket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (mSocket != null) {
            mSocket.on(Socket.EVENT_CONNECT, args -> {
                // Sử dụng Handler để cập nhật UI từ Service
                Log.d("SocketService", "Kết nối thành công.");
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplicationContext(), "Kết nối thành công đến: " + url, Toast.LENGTH_SHORT).show()
                );
            });

            mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("SocketService", "Socket connection error.", args[0] instanceof Exception ? (Exception) args[0] : null);
                }
            });

            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.w("SocketService", "Socket disconnected.");
                }
            });
            mSocket.connect();



            mSocket.on("new_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0) {
                        String message = args[0].toString();
                        Log.d("SocketService", "Received message: " + message);
                        showNotification(message);
                    } else {
                        Log.w("SocketService", "Received empty message.");
                    }
                }
            });

            mSocket.on("button_clicked", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String message = (String) args[0];
                    System.out.println("Received from server: " + message);

                    String phoneNumber = extractPhoneNumber(message);

                    showNotification("Gửi OTP thành công");

                    sendSms(phoneNumber, "Mã OTP của bạn là: 123456");
                }
            });

            // Lắng nghe sự kiện từ server
            mSocket.on("toggle_device_event", args -> {
                if (args.length > 0) {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("SocketService", "Received data: " + data.toString());
                    // Xử lý dữ liệu nhận được từ server tại đây
                }
            });

        }
    }

    // Phương thức để tắt thiết bị thông qua SocketIO
    public void toggleDevice(String amount, String transactionCode) {
        if (mSocket != null && mSocket.connected()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("amount", amount);
                jsonObject.put("transactionCode", transactionCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("toggle_device_event", jsonObject);
            Log.d("SocketService", "Emitted toggle_device_event: " + jsonObject.toString());
        } else {
            Log.e("SocketService", "Socket is not connected. Cannot emit toggle_device_event.");
        }
    }

    private String extractPhoneNumber(String message) {
        String phoneNumber = "";
        if (message != null && message.contains("Phone number: ")) {
            phoneNumber = message.split("Phone number: ")[1].trim();
        }
        return phoneNumber;
    }
    public void sendSms(String phoneNumber, String message) {
        try {
            // Get the SmsManager system service
            SmsManager smsManager = SmsManager.getDefault();
            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void sendSms(String phoneNumber, String message) {
//        try {
//            // Tạo intent để điều hướng người dùng tới ứng dụng nhắn tin mặc định
//            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
//            smsIntent.setData(Uri.parse("smsto:" + phoneNumber)); // Số điện thoại
//            smsIntent.putExtra("sms_body", message); // Nội dung tin nhắn
//
//            // Kiểm tra xem có ứng dụng nào xử lý intent hay không
//            if (smsIntent.resolveActivity(getPackageManager()) != null) {
//                startActivity(smsIntent); // Mở ứng dụng nhắn tin
//            } else {
//                Toast.makeText(getApplicationContext(), "No messaging app found!", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Failed to send SMS!", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String sender = intent.getStringExtra("sender");
            String message = intent.getStringExtra("message");
            if (sender != null && message != null) {
                try {
                    TransactionInfo transactionInfo = parseMessage(message);

                    if (transactionInfo.getOrderID() != null && !transactionInfo.getOrderID().isEmpty()) {

                        // Tạo đối tượng TransactionRequest
                        TransactionRequest transactionRequest = new TransactionRequest(
                                transactionInfo.getOrderID(),
                                Integer.parseInt(transactionInfo.getAmount().replace(".", "").replace(",", "")),
                                transactionInfo.getTransactionCode()
                        );
                        transactionRequest.setSmsFull(message);
                        transactionRequest.setSender(sender);
                        new TransactionTask().execute(transactionRequest);
                    }else {
                        TransactionRequest transactionRequest = new TransactionRequest();
                        transactionRequest.setSmsFull(message);
                        transactionRequest.setSender(sender);
                        transactionRequest.setAmount(Integer.parseInt(transactionInfo.getAmount().replace(".", "").replace(",", "")));

                        new TransactionTask2().execute(transactionRequest);
                    }
                }catch (Exception e) {
                    TransactionRequest transactionRequest = new TransactionRequest();
                    transactionRequest.setSmsFull(message);
                    transactionRequest.setSender(sender);

                    new TransactionTask2().execute(transactionRequest);
                }



            }
        }

        return START_STICKY;
    }



    // Thêm lớp AsyncTask để thực hiện các tác vụ mạng ngoài main thread
    private class TransactionTask extends AsyncTask<TransactionRequest, Void, TransactionResponse2> {

        @Override
        protected TransactionResponse2 doInBackground(TransactionRequest... transactionRequests) {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

            String token = "";

            LoginResponse.Data userData = SharedPreferencesUtil.getUserData(getApplicationContext());
            if (userData != null) {
                token = userData.getToken();
            }

//            Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), "token", Toast.LENGTH_SHORT).show();
//            Log.d("VVVVV", token);
//            Log.d("AAAAA", "token");
            // Lấy transactionRequest từ tham số đầu vào
            TransactionRequest transactionRequest = transactionRequests[0];

            Call<TransactionResponse2> call = apiService.createTransaction("Bearer " + token, transactionRequest);

            try {
                // Thực hiện gọi API đồng bộ trong doInBackground()
                Response<TransactionResponse2> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body();  // Trả về TransactionResponse2 nếu thành công
                } else {
                    return null;  // Trả về null nếu thất bại
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;  // Trả về null nếu có lỗi
            }
        }

        @Override
        protected void onPostExecute(TransactionResponse2 transactionResponse) {
            super.onPostExecute(transactionResponse);
            if (transactionResponse != null && transactionResponse.isSuccess()) {
                Log.d("TransactionTask", "Transaction success: " + transactionResponse.getMessage());
                
                // Lấy thông tin từ response
                if (transactionResponse.getData() != null) {
                    int durationMinutes = transactionResponse.getData().getDurationMinutes();
                    boolean isCompleteOrder = transactionResponse.getData().isCompleteOrder();
                    int remainAmount = transactionResponse.getData().getRemainAmount();
                    
                    // Lấy mã máy giặt từ response
                    String deviceId = transactionResponse.getData().getMaMayGiat();
                    
                    // Gửi broadcast để cập nhật UI
                    if (deviceId != null && !deviceId.isEmpty()) {
                        sendDeviceStatusBroadcast(deviceId, durationMinutes);
                        Log.d("TransactionTask", "Starting device: " + deviceId + " for " + durationMinutes + " minutes");
                    } else {
                        Log.e("TransactionTask", "Device ID is missing in response");
                    }
                }
            } else {
                Log.e("TransactionTask", "Transaction failed or response is null");
            }
        }
    }

    private class TransactionTask2 extends AsyncTask<TransactionRequest, Void, TransactionResponse2> {

        @Override
        protected TransactionResponse2 doInBackground(TransactionRequest... transactionRequests) {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

            String token = "";

            LoginResponse.Data userData = SharedPreferencesUtil.getUserData(getApplicationContext());
            if (userData != null) {
                token = userData.getToken();
            }

            TransactionRequest transactionRequest = transactionRequests[0];

            Call<TransactionResponse2> call = apiService.createTransaction("Bearer " + token, transactionRequest);

            try {
                // Thực hiện gọi API đồng bộ trong doInBackground()
                Response<TransactionResponse2> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body();  // Trả về TransactionResponse2 nếu thành công
                } else {
                    return null;  // Trả về null nếu thất bại
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;  // Trả về null nếu có lỗi
            }
        }

        @Override
        protected void onPostExecute(TransactionResponse2 transactionResponse) {
            super.onPostExecute(transactionResponse);
            if (transactionResponse != null && transactionResponse.isSuccess()) {
                Log.d("TransactionTask2", "Transaction success: " + transactionResponse.getMessage());
                
                // Lấy thông tin từ response
                if (transactionResponse.getData() != null) {
                    int durationMinutes = transactionResponse.getData().getDurationMinutes();
                    boolean isCompleteOrder = transactionResponse.getData().isCompleteOrder();
                    int remainAmount = transactionResponse.getData().getRemainAmount();
                    
                    // Lấy mã máy giặt từ response
                    String deviceId = transactionResponse.getData().getMaMayGiat();
                    
                    // Gửi broadcast để cập nhật UI
                    if (deviceId != null && !deviceId.isEmpty()) {
                        sendDeviceStatusBroadcast(deviceId, durationMinutes);
                        Log.d("TransactionTask2", "Starting device: " + deviceId + " for " + durationMinutes + " minutes");
                    } else {
                        Log.e("TransactionTask2", "Device ID is missing in response");
                    }
                }
            } else {
                Log.e("TransactionTask2", "Transaction failed or response is null");
            }
        }
    }



    private void showToast(final String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Gửi broadcast tới MainActivity để cập nhật UI dựa trên thời gian của thiết bị
     * @param deviceId ID của thiết bị cần cập nhật
     * @param durationMinutes Thời gian (phút) hoạt động của thiết bị
     */
    private void sendDeviceStatusBroadcast(String deviceId, int durationMinutes) {
        Log.d("SocketService", "Sending device status broadcast: " + deviceId + ", duration: " + durationMinutes);
        
        // Hủy timer cũ nếu có
        if (deviceTimers.containsKey(deviceId)) {
            CountDownTimer oldTimer = deviceTimers.get(deviceId);
            if (oldTimer != null) {
                oldTimer.cancel();
            }
        }
        
        // Gửi broadcast để bật thiết bị
        Intent intent = new Intent(DEVICE_STATUS_ACTION);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("isOn", true);
        intent.putExtra("durationMinutes", durationMinutes);
        sendBroadcast(intent);
        
        // Tạo một timer để tự động tắt thiết bị sau khi hết thời gian
        CountDownTimer timer = new CountDownTimer(durationMinutes * 60 * 1000, 60000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Có thể gửi broadcast cập nhật thời gian còn lại
                int minutesRemaining = (int) (millisUntilFinished / 60000);
                Intent updateIntent = new Intent(DEVICE_STATUS_ACTION);
                updateIntent.putExtra("deviceId", deviceId);
                updateIntent.putExtra("isOn", true);
                updateIntent.putExtra("minutesRemaining", minutesRemaining);
                sendBroadcast(updateIntent);
            }

            @Override
            public void onFinish() {
                // Gửi broadcast tắt thiết bị khi hết thời gian
                Intent finishIntent = new Intent(DEVICE_STATUS_ACTION);
                finishIntent.putExtra("deviceId", deviceId);
                finishIntent.putExtra("isOn", false);
                sendBroadcast(finishIntent);
                
                // Xóa timer khỏi map
                deviceTimers.remove(deviceId);
            }
        };
        
        // Lưu timer vào map
        deviceTimers.put(deviceId, timer);
        
        // Bắt đầu timer
        timer.start();
    }
    public void onOff(int isOnOff, String deviceId) {
        if (mSocket != null && mSocket.connected()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isOnOff", isOnOff);
                jsonObject.put("deviceId", deviceId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("on_off_device", jsonObject);
            Log.d("SocketService", "Emitted onOff_toggle_device_event: " + jsonObject.toString());
        } else {
            Log.e("SocketService", "Socket is not connected. Cannot emit toggle_device_event.");
        }
    }

    public TransactionInfo parseMessage(String message) {
        // Biểu thức chính quy để tìm số tiền và mã giao dịch
        String regex = "\\+([0-9,.]+)\\sVND";
        // Compile biểu thức chính quy
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);


        String regex2 = "([A-Za-z0-9]{4})\\s([A-Za-z0-9]{4})\\s([A-Za-z0-9]{10})";
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher2 = pattern2.matcher(message);

        // Kiểm tra và lấy kết quả
        if (matcher.find()) {
            String amount = matcher.group(1);  // Số tiền

            String orderid = "";
            if(matcher2.find()) {
                String transactionCode = matcher2.group(0);  // Mã giao dịch
                orderid = matcher2.group(3);
                return new TransactionInfo(amount, transactionCode, orderid);
            }
            return null;

            // Trả về đối tượng TransactionInfo chứa số tiền và mã giao dịch
        } else {
            // Xử lý nếu không tìm thấy dữ liệu
//            Toast.makeText(getApplicationContext(), "Không tìm thấy thông tin giao dịch.", Toast.LENGTH_SHORT).show();
            return null;  // Trả về null nếu không tìm thấy thông tin
        }
    }


    private void emitSmsToServer(String sender, String message) {
        if (mSocket != null && mSocket.connected()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("sender", sender);
                jsonObject.put("message", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("sms_received", jsonObject);
            Log.d("SocketService", "Emitted SMS to server: " + jsonObject.toString());
        } else {
            Log.e("SocketService", "Socket is not connected. Cannot emit SMS.");
        }
    }


    private void showNotification(String message) {
        // Tạo thông báo hiển thị khi nhận được dữ liệu từ server
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New Message")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background) // Đổi thành icon thực tế trong ứng dụng của bạn
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(2, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Ngắt kết nối khi Service bị hủy
        if (mSocket != null && mSocket.connected()) {
            mSocket.disconnect();
        }
    }
}
