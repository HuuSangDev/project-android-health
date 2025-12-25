package com.example.app_selfcare.upload;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_selfcare.R;

public class InforGoal extends AppCompatActivity {

    private CheckBox checkboxLoseWeight, checkboxGainWeight, checkboxIncreaseStrength;
    // Mặc định để rỗng hoặc giá trị mặc định tùy bạn
    private String selectedHealthGoal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Ánh xạ View (Đã sửa tên biến cho khớp ID XML)
        checkboxLoseWeight = findViewById(R.id.checkboxLoseWeight);
        checkboxGainWeight = findViewById(R.id.checkboxGainWeight);
        checkboxIncreaseStrength = findViewById(R.id.checkboxIncreaseStrength);

        // 2. Thiết lập logic chọn 1 trong 3 (Mapping value chuẩn yêu cầu)
        setupCheckboxLogic();

        // 3. Xử lý nút Tiếp tục
        findViewById(R.id.buttonContinue).setOnClickListener(v -> {
            if (selectedHealthGoal.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn một mục tiêu!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(InforGoal.this, Avatar.class);

            // --- Truyền dữ liệu cũ (Giữ nguyên logic của bạn) ---
            String gender = getIntent().getStringExtra("user_gender");
            if (gender != null) intent.putExtra("user_gender", gender);

            int age = getIntent().getIntExtra("user_age", -1);
            if (age != -1) intent.putExtra("user_age", age);

            int height = getIntent().getIntExtra("user_height_cm", -1);
            if (height != -1) intent.putExtra("user_height_cm", height);

            int weight = getIntent().getIntExtra("user_weight_kg", -1);
            if (weight != -1) intent.putExtra("user_weight_kg", weight);

            // --- Truyền dữ liệu MỚI (Value đã map: WEIGHT_LOSS, MAINTAIN, WEIGHT_GAIN) ---
            intent.putExtra("user_health_goal", selectedHealthGoal);

            // Log kiểm tra
            android.util.Log.d("InforGoal", "Goal selected: " + selectedHealthGoal);

            startActivity(intent);
            // Không finish() nếu bạn muốn user có thể back lại sửa
            // finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Nút Back
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void setupCheckboxLogic() {
        // Logic cho Giảm cân -> WEIGHT_LOSS
        checkboxLoseWeight.setOnClickListener(v -> {
            if (checkboxLoseWeight.isChecked()) {
                updateSelection(checkboxLoseWeight, "WEIGHT_LOSS");
            } else {
                selectedHealthGoal = "";
            }
        });

        // Logic cho Tăng cân -> WEIGHT_GAIN
        checkboxGainWeight.setOnClickListener(v -> {
            if (checkboxGainWeight.isChecked()) {
                updateSelection(checkboxGainWeight, "WEIGHT_GAIN");
            } else {
                selectedHealthGoal = "";
            }
        });

        // Logic cho Tăng sức bền (Giữ dáng) -> MAINTAIN
        checkboxIncreaseStrength.setOnClickListener(v -> {
            if (checkboxIncreaseStrength.isChecked()) {
                updateSelection(checkboxIncreaseStrength, "MAINTAIN");
            } else {
                selectedHealthGoal = "";
            }
        });
    }

    // Hàm helper để bỏ chọn các checkbox còn lại
    private void updateSelection(CheckBox selectedCheckbox, String goalValue) {
        selectedHealthGoal = goalValue;

        // Nếu chọn cái này thì bỏ chọn 2 cái kia
        if (selectedCheckbox != checkboxLoseWeight) checkboxLoseWeight.setChecked(false);
        if (selectedCheckbox != checkboxGainWeight) checkboxGainWeight.setChecked(false);
        if (selectedCheckbox != checkboxIncreaseStrength) checkboxIncreaseStrength.setChecked(false);
    }
}