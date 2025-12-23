package com.example.app_selfcare.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.R;
import com.example.app_selfcare.utils.LocaleManager;
import com.example.app_selfcare.utils.ThemeManager;

/**
 * Base Activity hỗ trợ đa ngôn ngữ và Dark Mode
 */
public abstract class BaseLocaleActivity extends AppCompatActivity {

    protected LocaleManager localeManager;
    protected ThemeManager themeManager;
    private View loadingView;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Áp dụng ngôn ngữ đã lưu
        LocaleManager localeManager = new LocaleManager(newBase);
        Context context = localeManager.applyLanguage(newBase);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Khởi tạo managers
        localeManager = new LocaleManager(this);
        themeManager = new ThemeManager(this);
        
        // Áp dụng theme
        themeManager.applySavedTheme();
        
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        View loading = LayoutInflater.from(this).inflate(R.layout.layout_global_loading, null);

        FrameLayout root = new FrameLayout(this);
        root.addView(contentView);
        root.addView(loading);

        super.setContentView(root);
        loadingView = loading;
    }

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

    /**
     * Đổi ngôn ngữ và restart activity
     */
    protected void changeLanguage(String languageCode) {
        localeManager.setLanguage(languageCode);
        recreate();
    }

    /**
     * Toggle ngôn ngữ
     */
    protected void toggleLanguage() {
        localeManager.toggleLanguage();
        recreate();
    }
}
