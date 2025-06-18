package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.example.myapplication.model.LoginResponse;

public class SharedPreferencesUtil {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String USER_DATA_KEY = "USER_DATA";

    public static void saveUserData(Context context, LoginResponse.Data data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        editor.putString(USER_DATA_KEY, jsonData);
        editor.apply();
    }
    public static LoginResponse.Data getUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jsonData = sharedPreferences.getString(USER_DATA_KEY, null);

        if (jsonData != null) {
            Gson gson = new Gson();
            return gson.fromJson(jsonData, LoginResponse.Data.class);
        }

        return null;
    }
    public static void clearUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_DATA_KEY);
        editor.apply();
    }

    private static final String DEFAULT_MINUTES_KEY = "DEFAULT_MINUTES";

    public static void saveDefaultMinutes(Context context, int minutes) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(DEFAULT_MINUTES_KEY, minutes).apply();
    }

    public static int getDefaultMinutes(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(DEFAULT_MINUTES_KEY, 55); // Mặc định là 48
    }

}