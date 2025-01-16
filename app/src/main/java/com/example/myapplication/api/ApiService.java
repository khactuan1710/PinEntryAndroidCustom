package com.example.myapplication.api;

import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.TransactionRequest;
import com.example.myapplication.model.TransactionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/turndevices")
    Call<ApiResponse> toggleDevice(
            @Header("Authorization") String token, // Add token in the header
            @Body DeviceRequest deviceRequest
    );


    @POST("/transaction")
    Call<TransactionResponse> createTransaction(
            @Header("Authorization") String token,
            @Body TransactionRequest request);

    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("/devices")
    Call<DeviceResponse> getDevices(@Header("Authorization") String authToken);

}
