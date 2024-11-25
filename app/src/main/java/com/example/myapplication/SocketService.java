package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class SocketService extends Service {

    private Socket mSocket;
    public static final String CHANNEL_ID = "socket_channel_id";
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
                .setContentTitle("Socket Service Running")
                .setContentText("Listening to socket events...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Đổi thành icon thực tế trong ứng dụng của bạn
                .build();

        startForeground(1, notification);

        // Kết nối tới server
        connectSocket(socketUrl);


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
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("SocketService", "Socket connected successfully.");
                }
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String sender = intent.getStringExtra("sender");
            String message = intent.getStringExtra("message");

            if (sender != null && message != null) {
                emitSmsToServer(sender, message);
            }

            String newUrl = intent.getStringExtra("socket_url");
            if (newUrl != null && !newUrl.isEmpty()) {
                socketUrl = newUrl;
                connectSocket(socketUrl);
            }
        }

        return START_STICKY;
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
