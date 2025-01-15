package com.example.myapplication.api;

import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.TransactionRequest;
import com.example.myapplication.model.TransactionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/turndevices")
    Call<ApiResponse> toggleDevice(
            @Body DeviceRequest deviceRequest
    );

    @POST("/transaction")
    Call<TransactionResponse> createTransaction(@Body TransactionRequest request);

}
