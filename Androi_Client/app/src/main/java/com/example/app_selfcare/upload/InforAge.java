package com.example.app_selfcare.upload;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_selfcare.R;

public class InforAge extends AppCompatActivity {

    // Thiết lập giới hạn tuổi
    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 100;

    private TextView textViewDisplayedAge;
    private HorizontalScrollView ageRulerScroll;
    private LinearLayout ageRulerContent;

    private int currentAge = 25; // Mặc định 25 tuổi
    private int tickSpacingPx;
    private int sidePaddingPx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_infor_age);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View
        textViewDisplayedAge = findViewById(R.id.textViewDisplayedAge);
        ageRulerScroll = findViewById(R.id.ageRulerScroll);
        ageRulerContent = findViewById(R.id.ageRulerContent);

        tickSpacingPx = dpToPx(12);
        sidePaddingPx = getResources().getDisplayMetrics().widthPixels / 2;

        rebuildAgeRuler();

        // Listener cuộn
        if (ageRulerScroll != null) {
            ageRulerScroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                handleAgeScroll(scrollX);
            });
        }

        // Nút Tiếp tục → Chuyển sang màn hình Chiều cao
        View btnContinue = findViewById(R.id.buttonContinue);
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                Intent intent = new Intent(InforAge.this, InforHeight.class);
                // Pass forward data from previous activities
                String gender = getIntent().getStringExtra("user_gender");
                if (gender != null) {
                    intent.putExtra("user_gender", gender);
                }
                // Pass current data
                intent.putExtra("user_age", currentAge);
                
                // Debug log
                android.util.Log.d("InforAge", "Passing gender: " + gender + ", age: " + currentAge);
                
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        // Nút Back
        View btnBack = findViewById(R.id.backButton);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }
    }

    private void rebuildAgeRuler() {
        if (ageRulerContent == null) return;

        ageRulerContent.removeAllViews();
        addEdgeSpacer(ageRulerContent);

        // Vòng lặp tạo vạch thước cho tuổi từ MIN_AGE đến MAX_AGE
        for (int age = MIN_AGE; age <= MAX_AGE; age++) {
            ageRulerContent.addView(createTickView(age));
        }
        addEdgeSpacer(ageRulerContent);

        updateAgeDisplay(currentAge);
        scrollToValue(currentAge);
    }

    private void handleAgeScroll(int scrollX) {
        float effective = Math.max(0, scrollX);

        float maxScroll = (MAX_AGE - MIN_AGE) * tickSpacingPx;
        if (effective > maxScroll) effective = maxScroll;

        int ageValue = MIN_AGE + Math.round(effective / tickSpacingPx);
        currentAge = clamp(ageValue, MIN_AGE, MAX_AGE);
        updateAgeDisplay(currentAge);
    }

    private void scrollToValue(int age) {
        if (ageRulerScroll == null) return;
        int scrollX = (int) ((age - MIN_AGE) * tickSpacingPx);
        ageRulerScroll.post(() -> ageRulerScroll.scrollTo(scrollX, 0));
    }

    private void updateAgeDisplay(int age) {
        if (textViewDisplayedAge == null) return;
        textViewDisplayedAge.setText(String.format("%d tuổi", age));
    }

    private View createTickView(int age) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tickSpacingPx, LinearLayout.LayoutParams.MATCH_PARENT);
        container.setLayoutParams(params);

        View line = new View(this);
        // Vạch dài cho các tuổi chia hết cho 5, vạch ngắn cho các tuổi khác
        boolean isMajorTick = (age % 5 == 0);

        int lineHeight = dpToPx(isMajorTick ? 60 : 30);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(dpToPx(2), lineHeight);
        lineParams.gravity = Gravity.CENTER_HORIZONTAL;
        line.setLayoutParams(lineParams);
        line.setBackgroundColor(Color.WHITE);
        container.addView(line);

        // Hiển thị số dưới vạch cho các vạch chính
        if (isMajorTick) {
            TextView label = new TextView(this);
            label.setText(String.valueOf(age));
            label.setTextColor(Color.WHITE);
            label.setAlpha(0.85f);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            label.setGravity(Gravity.CENTER_HORIZONTAL);
            container.addView(label);
        }

        return container;
    }

    private void addEdgeSpacer(LinearLayout container) {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(sidePaddingPx, LinearLayout.LayoutParams.MATCH_PARENT));
        container.addView(spacer);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}

