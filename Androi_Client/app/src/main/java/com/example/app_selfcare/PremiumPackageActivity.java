package com.example.app_selfcare;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;

public class PremiumPackageActivity extends AppCompatActivity {

    private RadioButton rbMonthly, rbYearly;
    private CardView cardMonthly, cardYearly;
    private MaterialButton btnSubscribe;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        initViews();
        setupListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        rbMonthly = findViewById(R.id.rbMonthly);
        rbYearly = findViewById(R.id.rbYearly);
        cardMonthly = findViewById(R.id.cardMonthly);
        cardYearly = findViewById(R.id.cardYearly);
        btnSubscribe = findViewById(R.id.btnSubscribe);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        // Card click to select plan
        cardMonthly.setOnClickListener(v -> {
            rbMonthly.setChecked(true);
            rbYearly.setChecked(false);
        });

        cardYearly.setOnClickListener(v -> {
            rbYearly.setChecked(true);
            rbMonthly.setChecked(false);
        });

        // Radio button listeners
        rbMonthly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) rbYearly.setChecked(false);
        });

        rbYearly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) rbMonthly.setChecked(false);
        });

        // Subscribe button
        btnSubscribe.setOnClickListener(v -> {
            String plan = rbYearly.isChecked() ? "Gói năm - 699.000đ" : "Gói tháng - 99.000đ";
            Toast.makeText(this, "Đang xử lý thanh toán: " + plan, Toast.LENGTH_SHORT).show();
            // TODO: Implement payment logic
        });
    }
}