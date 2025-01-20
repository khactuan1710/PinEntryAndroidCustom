package com.example.myapplication.api;

import com.example.myapplication.model.ApiResponse;
import com.example.myapplication.model.BankCodeResponse;
import com.example.myapplication.model.ChangePasswordRequest;
import com.example.myapplication.model.CreateDeviceRequest;
import com.example.myapplication.model.DeviceRequest;
import com.example.myapplication.model.DeviceResponse;
import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.LoginResponse;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.SimpleResult;
import com.example.myapplication.model.TransactionRequest;
import com.example.myapplication.model.TransactionResponse;
import com.example.myapplication.model.UpdateDeviceRequest;
import com.example.myapplication.model.UpdateUserRequest;
import com.example.myapplication.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("/users")
    Call<UserResponse> getUsers(@Header("Authorization") String authToken);

    @PUT("/users/{userId}")
    Call<SimpleResult> updateUser(
            @Header("Authorization") String token,   // Token để xác thực
            @Path("userId") String userId,          // Truyền ID người dùng trong URL
            @Body UpdateUserRequest updateUserRequest // Dữ liệu cần cập nhật
    );

    @GET("/bankcodes")
    Call<BankCodeResponse> getBankCodes();

    @POST("/register")
    Call<SimpleResult> register(
            @Header("Authorization") String token,
            @Body RegisterRequest registerRequest
    );

    @POST("/devices")
    Call<SimpleResult> registerDevice(
            @Header("Authorization") String token,
            @Body CreateDeviceRequest deviceRequest
    );

    @GET("/devices")
    Call<DeviceResponse> getDevicesByHost(
            @Header("Authorization") String authToken,  // Token để xác thực
            @Query("hostID") String hostID
    );

    @POST("/change-password")
    Call<SimpleResult> changePassword(
            @Header("Authorization") String token,      // Token để xác thực
            @Body ChangePasswordRequest changePasswordRequest // Dữ liệu yêu cầu
    );

    @PUT("/devices/{deviceID}")
    Call<SimpleResult> updateDevice(
            @Header("Authorization") String token,  // Token xác thực
            @Path("deviceID") String deviceID,      // ID của thiết bị cần cập nhật
            @Body UpdateDeviceRequest updateDeviceRequest // Dữ liệu yêu cầu cập nhật
    );



}
