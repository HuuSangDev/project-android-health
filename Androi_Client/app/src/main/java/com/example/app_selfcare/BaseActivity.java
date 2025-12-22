package com.example.app_selfcare;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private View loadingView;

    @Override
    public void setContentView(int layoutResID) {
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
