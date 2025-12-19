package com.example.app_selfcare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

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

    // CHỈ BẬT LOADING
    public void showLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    // CHỈ TẮT LOADING
    public void hideLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }
}

