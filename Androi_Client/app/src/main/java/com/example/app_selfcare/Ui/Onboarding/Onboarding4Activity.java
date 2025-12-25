package com.example.app_selfcare.Ui.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_selfcare.LoginActivity;
import com.example.app_selfcare.R;

public class Onboarding4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force light mode for onboarding screens
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.trang4), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút Bắt đầu → Chuyển sang Login
        findViewById(R.id.btnGetStarted).setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding4Activity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng toàn bộ onboarding
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Link "Đăng nhập" → Chuyển sang Login
        findViewById(R.id.tvSignInLink).setOnClickListener(v -> {
            Intent intent = new Intent(Onboarding4Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Nút Back (nếu có trong layout) → Quay lại Onboarding3
        // (Thêm nếu layout có btnBack, ví dụ findViewById(R.id.btnBack))
        /*
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        */
    }
}