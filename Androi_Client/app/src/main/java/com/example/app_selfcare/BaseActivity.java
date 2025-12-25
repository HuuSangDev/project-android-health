package com.example.app_selfcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.Data.remote.WebSocketManager;
import com.example.app_selfcare.utils.InAppNotificationManager;
import com.example.app_selfcare.utils.LocaleManager;
import com.example.app_selfcare.utils.ThemeManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity implements WebSocketManager.NotificationListener {

    private static final String TAG = "BaseActivity";
    private static final String PREF_NAME = "APP_DATA";
    private static final String KEY_USER_GOAL = "USER_GOAL";

    private View loadingView;
    protected LocaleManager localeManager;
    protected ThemeManager themeManager;
    
    // In-app notification manager
    protected InAppNotificationManager inAppNotificationManager;
    
    // API Service
    private ApiService apiService;

    @Override
    protected void attachBaseContext(Context newBase) {
        localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        inAppNotificationManager = new InAppNotificationManager(this);
        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (themeManager == null) {
            themeManager = new ThemeManager(this);
        }
        themeManager.applySavedTheme();
        
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        View loading = LayoutInflater.from(this)
                .inflate(R.layout.layout_global_loading, null);

        FrameLayout root = new FrameLayout(this);
        root.addView(contentView);
        root.addView(loading);

        super.setContentView(root);

        loadingView = loading;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Kết nối WebSocket với goal của user
        connectWebSocketWithGoal();
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Hủy đăng ký listener khi activity không còn visible
        WebSocketManager.getInstance().setNotificationListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (inAppNotificationManager != null) {
            inAppNotificationManager.destroy();
        }
    }

    /**
     * Kết nối WebSocket với goal của user
     */
    private void connectWebSocketWithGoal() {
        WebSocketManager wsManager = WebSocketManager.getInstance();
        wsManager.setNotificationListener(this);

        // Lấy goal đã lưu từ SharedPreferences
        String savedGoal = getSavedGoal();
        
        if (savedGoal != null && !savedGoal.isEmpty()) {
            // Đã có goal, kết nối luôn
            if (!wsManager.isConnected() || !savedGoal.equals(wsManager.getCurrentGoal())) {
                wsManager.connect(savedGoal);
            }
        } else {
            // Chưa có goal, gọi API lấy goal
            fetchAndConnectWithGoal(wsManager);
        }
    }

    /**
     * Gọi API lấy goal và kết nối WebSocket
     */
    private void fetchAndConnectWithGoal(WebSocketManager wsManager) {
        apiService.getMyGoal().enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    String goal = response.body().getResult();
                    Log.d(TAG, "Fetched user goal: " + goal);
                    
                    // Lưu goal vào SharedPreferences
                    saveGoal(goal);
                    
                    // Kết nối WebSocket với goal
                    wsManager.connect(goal);
                } else {
                    Log.w(TAG, "Failed to fetch goal, using default");
                    // Fallback: kết nối với goal mặc định
                    wsManager.connect("WEIGHT_LOSS");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Log.e(TAG, "Error fetching goal", t);
                // Fallback: kết nối với goal mặc định
                wsManager.connect("WEIGHT_LOSS");
            }
        });
    }

    /**
     * Lưu goal vào SharedPreferences
     */
    private void saveGoal(String goal) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_GOAL, goal).apply();
    }

    /**
     * Lấy goal đã lưu từ SharedPreferences
     */
    private String getSavedGoal() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_USER_GOAL, null);
    }

    /**
     * Cập nhật goal mới (gọi khi user thay đổi goal trong profile)
     */
    public void updateUserGoal(String newGoal) {
        saveGoal(newGoal);
        WebSocketManager.getInstance().changeGoal(newGoal);
        Log.d(TAG, "Updated user goal to: " + newGoal);
    }

    // ==================== WebSocket Notification Listener ====================

    @Override
    public void onNotificationReceived(String type, Long targetId, String title, String message) {
        Log.d(TAG, "Notification received: " + title + " - " + message);
        
        if (inAppNotificationManager != null) {
            inAppNotificationManager.showNotification(type, targetId, title, message);
        }
    }

    @Override
    public void onConnectionStateChanged(boolean connected) {
        Log.d(TAG, "WebSocket connection state: " + (connected ? "Connected" : "Disconnected"));
    }

    // ==================== Loading Methods ====================

    public void showLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }
}
