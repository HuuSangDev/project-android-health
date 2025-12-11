// app/src/main/java/com/example/app_selfcare/Ui/Admin/AddExerciseActivity.java
package com.example.app_selfcare;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.Data.Model.Exercise;
import com.example.app_selfcare.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddExerciseActivity extends AppCompatActivity {

    private TextInputEditText etExerciseName;
    private TextInputEditText etExerciseInfo;
    private TextInputEditText etExerciseDuration;
    private TextInputEditText etExerciseLevel;

    private MaterialButton btnSaveExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise); // Layout bạn vừa gửi

        initViews();
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
        etExerciseName = findViewById(R.id.etExerciseName);
        etExerciseInfo = findViewById(R.id.etExerciseInfo);
        etExerciseDuration = findViewById(R.id.etExerciseDuration);
        etExerciseLevel = findViewById(R.id.etExerciseLevel);
        btnSaveExercise = findViewById(R.id.btnSaveExercise);
    }

    private void setupToolbar() {
        // Back button trong header (bạn đã có ImageView backButton)
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Nếu muốn dùng ActionBar (tùy chọn)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thêm bài tập");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupClickListeners() {
        btnSaveExercise.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        String name = etExerciseName.getText().toString().trim();
        String info = etExerciseInfo.getText().toString().trim();
        String durationStr = etExerciseDuration.getText().toString().trim();
        String level = etExerciseLevel.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etExerciseName.setError("Vui lòng nhập tên bài tập");
            etExerciseName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(durationStr)) {
            etExerciseDuration.setError("Vui lòng nhập thời lượng");
            etExerciseDuration.requestFocus();
            return;
        }

        int duration = 30; // mặc định
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            etExerciseDuration.setError("Thời lượng phải là số");
            etExerciseDuration.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(level)) {
            etExerciseLevel.setError("Vui lòng nhập độ khó");
            etExerciseLevel.requestFocus();
            return;
        }

        // Tạo object Exercise mới
        Exercise newExercise = new Exercise();
        newExercise.setName(name);
        newExercise.setDescription(info.isEmpty() ? "Bài tập tốt cho sức khỏe" : info);
        newExercise.setDurationMinutes(duration);
        newExercise.setCaloriesBurned(duration * 10); // ước lượng đơn giản
        newExercise.setDifficulty(level);
        newExercise.setCategoryId("default"); // bạn có thể thêm Spinner sau
        newExercise.setImageResId(R.drawable.ic_active); // ảnh mặc định

        // TODO: Lưu vào database (Firebase, Room, v.v.)
        // Ví dụ: exerciseRepository.add(newExercise);

        Toast.makeText(this, "Thêm bài tập thành công!", Toast.LENGTH_SHORT).show();
        finish(); // quay lại danh sách
    }

    // Xử lý nút back trên toolbar (nếu dùng ActionBar)
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}