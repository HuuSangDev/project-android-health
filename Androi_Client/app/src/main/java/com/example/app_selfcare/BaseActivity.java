package com.example.app_selfcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.utils.LocaleManager;
import com.example.app_selfcare.utils.ThemeManager;

public abstract class BaseActivity extends AppCompatActivity {

    private View loadingView;
    protected LocaleManager localeManager;
    protected ThemeManager themeManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Áp dụng ngôn ngữ đã lưu cho Activity này
        localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
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
