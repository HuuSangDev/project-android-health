package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class PremiumIntroActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private TextView tvRestore;
    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_package);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnClose = findViewById(R.id.btnClose);
        tvRestore = findViewById(R.id.tvRestore);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupListeners() {
        // Close button
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }

        // Restore button
        if (tvRestore != null) {
            tvRestore.setOnClickListener(v -> {
                // TODO: Implement restore purchase logic
            });
        }

        // Continue button -> Go to Premium package selection
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                Intent intent = new Intent(PremiumIntroActivity.this, PremiumPackageActivity.class);
                startActivity(intent);
            });
        }
    }
}
