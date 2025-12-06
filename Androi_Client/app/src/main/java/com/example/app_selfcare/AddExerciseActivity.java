package com.example.app_selfcare;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddExerciseActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnSaveExercise;
    private TextInputEditText etExerciseName;
    private TextInputEditText etExerciseInfo;
    private TextInputEditText etExerciseDuration;
    private TextInputEditText etExerciseLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.backButton);
        btnSaveExercise = findViewById(R.id.btnSaveExercise);
        etExerciseName = findViewById(R.id.etExerciseName);
        etExerciseInfo = findViewById(R.id.etExerciseInfo);
        etExerciseDuration = findViewById(R.id.etExerciseDuration);
        etExerciseLevel = findViewById(R.id.etExerciseLevel);
    }

    private void setupClickListeners() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút lưu bài tập
        btnSaveExercise.setOnClickListener(v -> saveExercise());
    }

    private void saveExercise() {
        // Lấy dữ liệu từ các trường nhập
        String exerciseName = etExerciseName.getText() != null ? etExerciseName.getText().toString().trim() : "";
        String exerciseInfo = etExerciseInfo.getText() != null ? etExerciseInfo.getText().toString().trim() : "";
        String durationStr = etExerciseDuration.getText() != null ? etExerciseDuration.getText().toString().trim() : "";
        String level = etExerciseLevel.getText() != null ? etExerciseLevel.getText().toString().trim() : "";

        // Validate dữ liệu
        if (exerciseName.isEmpty()) {
            etExerciseName.setError("Vui lòng nhập tên bài tập");
            etExerciseName.requestFocus();
            return;
        }

        if (exerciseInfo.isEmpty()) {
            etExerciseInfo.setError("Vui lòng nhập mô tả bài tập");
            etExerciseInfo.requestFocus();
            return;
        }

        if (durationStr.isEmpty()) {
            etExerciseDuration.setError("Vui lòng nhập thời lượng");
            etExerciseDuration.requestFocus();
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                etExerciseDuration.setError("Thời lượng phải lớn hơn 0");
                etExerciseDuration.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etExerciseDuration.setError("Vui lòng nhập số hợp lệ");
            etExerciseDuration.requestFocus();
            return;
        }

        if (level.isEmpty()) {
            etExerciseLevel.setError("Vui lòng nhập độ khó");
            etExerciseLevel.requestFocus();
            return;
        }

        // TODO: Gửi dữ liệu lên server/API hoặc lưu vào database
        // Hiện tại chỉ hiển thị thông báo thành công
        Toast.makeText(this, "Đã lưu bài tập: " + exerciseName, Toast.LENGTH_SHORT).show();
        
        // Đóng activity sau khi lưu thành công
        finish();
    }
}


