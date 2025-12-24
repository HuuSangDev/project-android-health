package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.utils.ImageUtils;
import com.example.app_selfcare.utils.LocaleManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseIngredientsActivity extends AppCompatActivity {

    private static final String TAG = "ExerciseIngredients";
    
    private ImageView btnBack, exerciseImage;
    private TextView tvExerciseName, tvCaloriesBurned, tvDuration, tvDifficulty;
    private TextView tvMuscleGroup, tvEquipment, tvInstructions, tvBenefits;
    private ProgressBar progressBar;
    private CardView cardBurnInfo, cardDetails, cardInstructions;
    
    private int exerciseId;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_ingredients);

        // Lấy exerciseId từ Intent
        exerciseId = getIntent().getIntExtra("EXERCISE_ID", -1);
        if (exerciseId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin bài tập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupEvents();
        fetchExerciseDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        exerciseImage = findViewById(R.id.exerciseImage);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        
        // Burn info
        tvCaloriesBurned = findViewById(R.id.tvCaloriesBurned);
        tvDuration = findViewById(R.id.tvDuration);
        
        // Details
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvMuscleGroup = findViewById(R.id.tvMuscleGroup);
        tvEquipment = findViewById(R.id.tvEquipment);
        tvBenefits = findViewById(R.id.tvBenefits);
        
        // Instructions
        tvInstructions = findViewById(R.id.tvInstructions);
        
        // Cards and progress
        cardBurnInfo = findViewById(R.id.cardBurnInfo);
        cardDetails = findViewById(R.id.cardDetails);
        cardInstructions = findViewById(R.id.cardInstructions);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        
        // Click vào ảnh để xem phóng to
        exerciseImage.setOnClickListener(v -> showImageDetail());
    }

    private void showImageDetail() {
        // Tạm thời comment out vì chưa có ImageDetailActivity
        // ExerciseResponse exercise = (ExerciseResponse) exerciseImage.getTag();
        // if (exercise != null && exercise.getImageUrl() != null) {
        //     Intent intent = new Intent(this, ImageDetailActivity.class);
        //     intent.putExtra("IMAGE_URL", exercise.getImageUrl());
        //     intent.putExtra("TITLE", exercise.getExerciseName());
        //     startActivity(intent);
        // }
        Toast.makeText(this, "Tính năng xem ảnh chi tiết sẽ được cập nhật", Toast.LENGTH_SHORT).show();
    }

    private void fetchExerciseDetails() {
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        hideAllCards();

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getExerciseById(exerciseId, "Bearer " + token).enqueue(new Callback<ApiResponse<ExerciseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExerciseResponse>> call, 
                                 Response<ApiResponse<ExerciseResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    Toast.makeText(ExerciseIngredientsActivity.this, "Không thể tải thông tin bài tập", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                ExerciseResponse exercise = response.body().getResult();
                displayExerciseDetails(exercise);
                showAllCards();
            }

            @Override
            public void onFailure(Call<ApiResponse<ExerciseResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error fetching exercise details", t);
                Toast.makeText(ExerciseIngredientsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayExerciseDetails(ExerciseResponse exercise) {
        // Lưu exercise object vào tag của image để sử dụng khi click
        exerciseImage.setTag(exercise);
        
        // Tên bài tập
        tvExerciseName.setText(exercise.getExerciseName() != null ? exercise.getExerciseName() : "Không có tên");
        
        // Load ảnh
        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            ImageUtils.loadImageSafely(this, exercise.getImageUrl(), exerciseImage);
        } else {
            exerciseImage.setImageResource(R.drawable.ic_proflie);
        }
        
        // Thông tin đốt cháy
        tvCaloriesBurned.setText(String.format("%.0f kcal", exercise.getCaloriesPerMinute()));
        tvDuration.setText("Theo phút"); // Không có duration cụ thể trong response
        
        // Chi tiết bài tập
        tvDifficulty.setText(exercise.getDifficultyLevel() != null ? exercise.getDifficultyLevel() : "Không xác định");
        tvMuscleGroup.setText(exercise.getMuscleGroups() != null ? exercise.getMuscleGroups() : "Không xác định");
        tvEquipment.setText(exercise.getEquipmentNeeded() != null ? exercise.getEquipmentNeeded() : "Không cần dụng cụ");
        tvBenefits.setText(exercise.getDescription() != null ? exercise.getDescription() : "Không có thông tin");
        
        // Hướng dẫn
        if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
            tvInstructions.setText(exercise.getInstructions());
        } else {
            tvInstructions.setText("Không có hướng dẫn");
        }
    }

    private void hideAllCards() {
        cardBurnInfo.setVisibility(View.GONE);
        cardDetails.setVisibility(View.GONE);
        cardInstructions.setVisibility(View.GONE);
    }

    private void showAllCards() {
        cardBurnInfo.setVisibility(View.VISIBLE);
        cardDetails.setVisibility(View.VISIBLE);
        cardInstructions.setVisibility(View.VISIBLE);
    }
}