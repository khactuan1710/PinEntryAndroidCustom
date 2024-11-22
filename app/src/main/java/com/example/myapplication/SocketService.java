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
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class SocketService extends Service {

    private Socket mSocket;
    public static final String CHANNEL_ID = "socket_channel_id";

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
        try {
            mSocket = IO.socket("http://10.10.113.225:8080");
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
                    // Xử lý sự kiện khi nhận được thông điệp từ server
                    String message = (String) args[0];
                    System.out.println("Received from server: " + message);

                    // Giả sử message có dạng: "Button was clicked! Phone number: 0859864442"
                    // Tách số điện thoại từ message
                    String phoneNumber = extractPhoneNumber(message);

                    // Hiển thị thông báo (nếu cần)
                    showNotification("Gửi OTP thành công");

                    // Gửi SMS đến số điện thoại vừa nhận
                    sendSms(phoneNumber, "Mã OTP của bạn là: 123456");
                }
            });
        }

    }

    // Hàm tách số điện thoại từ message
    private String extractPhoneNumber(String message) {
        String phoneNumber = "";
        if (message != null && message.contains("Phone number: ")) {
            // Lấy số điện thoại sau từ khóa "Phone number: "
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
