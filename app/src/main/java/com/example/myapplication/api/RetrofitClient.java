package com.example.myapplication.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
//    public static String BASE_URL = "http://10.10.112.24:5001";
    public static String BASE_URL = "https://iot.mimi.sg";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Tạo HttpLoggingInterceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Ghi log toàn bộ (request và response)

            // Cấu hình OkHttpClient với HttpLoggingInterceptor và timeout 90 giây
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)  // Thêm logging interceptor
                    .connectTimeout(90, TimeUnit.SECONDS) // Thời gian kết nối tối đa
                    .readTimeout(90, TimeUnit.SECONDS)    // Thời gian đọc tối đa
                    .writeTimeout(90, TimeUnit.SECONDS)   // Thời gian ghi tối đa
                    .build();

            // Tạo Retrofit với OkHttpClient
            retrofit = new Retrofit.Builder()
//                    .baseUrl("https://iot.mimi.sg") // URL của server
                    .baseUrl(BASE_URL) // URL của server
                    .client(okHttpClient)          // Thêm OkHttpClient vào Retrofit
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
