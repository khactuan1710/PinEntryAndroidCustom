package com.example.myapplication.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Cấu hình OkHttpClient với timeout 90 giây
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(90, TimeUnit.SECONDS) // Thời gian kết nối tối đa
                    .readTimeout(90, TimeUnit.SECONDS)    // Thời gian đọc tối đa
                    .writeTimeout(90, TimeUnit.SECONDS)   // Thời gian ghi tối đa
                    .build();

            // Tạo Retrofit với OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://iot.mimi.sg") // URL của server
                    .client(okHttpClient)          // Thêm OkHttpClient vào Retrofit
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
