package com.example.app_selfcare;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.Data.remote.WebSocketManager;
import com.example.app_selfcare.utils.InAppNotificationManager;
import com.example.app_selfcare.utils.LocaleManager;
import com.example.app_selfcare.utils.ThemeManager;

public abstract class BaseActivity extends AppCompatActivity implements WebSocketManager.NotificationListener {

    private static final String TAG = "BaseActivity";

    private View loadingView;
    protected LocaleManager localeManager;
    protected ThemeManager themeManager;
    
    // In-app notification manager
    protected InAppNotificationManager inAppNotificationManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Áp dụng ngôn ngữ đã lưu cho Activity này
        localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Khởi tạo in-app notification manager
        inAppNotificationManager = new InAppNotificationManager(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        // Khởi tạo theme manager nếu chưa có
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
        
        // Kết nối WebSocket và đăng ký listener
        connectWebSocket();
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
        
        // Cleanup notification manager
        if (inAppNotificationManager != null) {
            inAppNotificationManager.destroy();
        }
    }

    /**
     * Kết nối WebSocket và đăng ký listener
     */
    private void connectWebSocket() {
        WebSocketManager wsManager = WebSocketManager.getInstance();
        wsManager.setNotificationListener(this);
        
        if (!wsManager.isConnected()) {
            wsManager.connect();
        }
    }

    // ==================== WebSocket Notification Listener ====================

    @Override
    public void onNotificationReceived(String type, Long targetId, String title, String message) {
        Log.d(TAG, "Notification received: " + title + " - " + message);
        
        // Hiển thị in-app notification bar
        if (inAppNotificationManager != null) {
            inAppNotificationManager.showNotification(type, targetId, title, message);
        }
    }

    @Override
    public void onConnectionStateChanged(boolean connected) {
        Log.d(TAG, "WebSocket connection state: " + (connected ? "Connected" : "Disconnected"));
    }

    // ==================== Loading Methods ====================

    // BẬT LOADING (DÙNG TEXT TRONG XML)
    public void showLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    // TẮT LOADING
    public void hideLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }
}
