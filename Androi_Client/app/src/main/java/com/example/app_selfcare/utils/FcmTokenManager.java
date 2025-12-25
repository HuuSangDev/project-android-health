package com.example.app_selfcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.app_selfcare.Data.Model.Request.DeviceTokenRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.DeviceTokenResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manager class để xử lý FCM token
 * - Lấy token từ Firebase
 * - Gửi token lên server
 * - Hủy đăng ký token khi logout
 */
public class FcmTokenManager {

    private static final String TAG = "FcmTokenManager";
    private static final String PREF_NAME = "APP_DATA";
    private static final String KEY_FCM_TOKEN = "FCM_TOKEN";
    private static final String KEY_TOKEN_SENT = "FCM_TOKEN_SENT";

    private final Context context;
    private final SharedPreferences prefs;

    public FcmTokenManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lấy FCM token và gửi lên server
     * Gọi sau khi login thành công
     */
    public void registerToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Lấy token mới
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    // Lưu token
                    saveToken(token);

                    // Gửi lên server
                    sendTokenToServer(token);
                });
    }

    /**
     * Gửi token lên server
     */
    public void sendTokenToServer(String fcmToken) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            Log.w(TAG, "FCM token is null or empty");
            return;
        }

        // Kiểm tra đã gửi token này chưa
        String sentToken = prefs.getString(KEY_TOKEN_SENT, "");
        if (fcmToken.equals(sentToken)) {
            Log.d(TAG, "Token already sent to server");
            return;
        }

        ApiService apiService = ApiClient.getClientWithToken(context).create(ApiService.class);
        DeviceTokenRequest request = new DeviceTokenRequest(fcmToken, "ANDROID");

        apiService.registerDeviceToken(request).enqueue(new Callback<ApiResponse<DeviceTokenResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DeviceTokenResponse>> call,
                                   Response<ApiResponse<DeviceTokenResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "FCM token registered successfully");
                    // Đánh dấu đã gửi token
                    prefs.edit().putString(KEY_TOKEN_SENT, fcmToken).apply();
                } else {
                    Log.e(TAG, "Failed to register FCM token: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DeviceTokenResponse>> call, Throwable t) {
                Log.e(TAG, "Error registering FCM token", t);
            }
        });
    }

    /**
     * Hủy đăng ký token khi logout
     */
    public void unregisterToken() {
        String fcmToken = getToken();
        if (fcmToken == null || fcmToken.isEmpty()) {
            Log.w(TAG, "No FCM token to unregister");
            clearTokenSentFlag();
            return;
        }

        ApiService apiService = ApiClient.getClientWithToken(context).create(ApiService.class);

        apiService.unregisterDeviceToken(fcmToken).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "FCM token unregistered successfully");
                } else {
                    Log.e(TAG, "Failed to unregister FCM token: " + response.code());
                }
                clearTokenSentFlag();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Error unregistering FCM token", t);
                clearTokenSentFlag();
            }
        });
    }

    /**
     * Hủy tất cả token của user (logout từ tất cả thiết bị)
     */
    public void unregisterAllTokens() {
        ApiService apiService = ApiClient.getClientWithToken(context).create(ApiService.class);

        apiService.unregisterAllDeviceTokens().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "All FCM tokens unregistered successfully");
                } else {
                    Log.e(TAG, "Failed to unregister all FCM tokens: " + response.code());
                }
                clearTokenSentFlag();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Error unregistering all FCM tokens", t);
                clearTokenSentFlag();
            }
        });
    }

    /**
     * Lưu FCM token
     */
    private void saveToken(String token) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    /**
     * Lấy FCM token đã lưu
     */
    public String getToken() {
        return prefs.getString(KEY_FCM_TOKEN, null);
    }

    /**
     * Xóa flag đã gửi token (để gửi lại khi login lần sau)
     */
    private void clearTokenSentFlag() {
        prefs.edit().remove(KEY_TOKEN_SENT).apply();
    }

    /**
     * Kiểm tra token đã được gửi lên server chưa
     */
    public boolean isTokenSent() {
        String sentToken = prefs.getString(KEY_TOKEN_SENT, "");
        String currentToken = getToken();
        return currentToken != null && currentToken.equals(sentToken);
    }
}
