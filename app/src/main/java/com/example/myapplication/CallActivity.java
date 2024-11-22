package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

public class CallActivity extends AppCompatActivity {

    AppCompatButton btn_call;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        btn_call = findViewById(R.id.btn_call);
        Activity activity  =this;

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Nếu chưa có quyền, yêu cầu cấp quyền
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    // Gọi điện thoại
                    callPhoneNumber("0343938411");  // Số điện thoại bạn muốn gọi
                }
            }
        });

        // Đăng ký listener để theo dõi trạng thái cuộc gọi
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    // Khi cuộc gọi đã bắt đầu (đã kết nối), phát âm thanh
                    playSound();
                } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                    // Cuộc gọi đang đổ chuông, có thể bạn muốn làm gì đó ở đây
                    playSound();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    // Cuộc gọi kết thúc (ngắt cuộc gọi)
                    playSound();
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    // Hàm gọi điện thoại
    private void callPhoneNumber(String phoneNumber) {
        // Kiểm tra quyền CALL_PHONE
        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm phát âm thanh
    private void playSound() {
        // Phát âm thanh khi bắt máy
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_file);  // Đặt file âm thanh vào thư mục res/raw
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);  // Đảm bảo âm thanh phát qua cuộc gọi
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký phoneStateListener khi không còn sử dụng
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }
}