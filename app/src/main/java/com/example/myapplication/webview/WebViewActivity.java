package com.example.myapplication.webview;


import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

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

        // Set tiêu đề
        TextView tvHeader = findViewById(R.id.tv_header);
        tvHeader.setText(header);

        // Cấu hình WebView
        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }
}
