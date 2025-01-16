package com.example.myapplication.webview;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(view -> {
            finish(); // Quay lại Activity trước đó
        });

        // Nhận URL từ Intent
        String url = getIntent().getStringExtra("url");
        String header = getIntent().getStringExtra("header");
        String token = getIntent().getStringExtra("token");

        // Set tiêu đề
        TextView tvHeader = findViewById(R.id.tv_header);
        tvHeader.setText(header);

        // Cấu hình WebView
        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token); // Thêm token vào header

        // Load URL với header
        webView.loadUrl(url, headers);

        // Thêm icon reload và xử lý reload
        ImageView ivReload = findViewById(R.id.iv_reload);
        ivReload.setOnClickListener(view -> {
            // Reload trang web
            webView.reload();
        });
    }
}
