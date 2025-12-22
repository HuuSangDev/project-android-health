package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutDetailActivity";

    // Views
    private ImageView ivExerciseImage;
    private TextView tvExerciseName, tvDifficulty, tvCalories;
    private TextView tvMuscleGroups, tvEquipment, tvDescription;
    private TextView tvInstructions, tvCategory;
    private ProgressBar progressBar;
    private View scrollView;

    private ApiService apiService;
    private int exerciseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_detail);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initApiService();
        setupNavigation();

        // Nhận exerciseId từ Intent
        exerciseId = getIntent().getIntExtra("exerciseId", -1);
        String exerciseName = getIntent().getStringExtra("exerciseName");

        if (exerciseId != -1) {
            loadExerciseDetail(exerciseId);
        } else if (exerciseName != null) {
            tvExerciseName.setText(exerciseName);
        }
    }

    private void initViews() {
        ivExerciseImage = findViewById(R.id.ivExerciseImage);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvCalories = findViewById(R.id.tvCalories);
        tvMuscleGroups = findViewById(R.id.tvMuscleGroups);
        tvEquipment = findViewById(R.id.tvEquipment);
        tvDescription = findViewById(R.id.tvDescription);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvCategory = findViewById(R.id.tvCategory);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
    }

    private void initApiService() {
        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
    }

    private void loadExerciseDetail(int id) {
        showLoading(true);

        apiService.getExerciseById(id).enqueue(new Callback<ApiResponse<ExerciseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExerciseResponse>> call,
                                   Response<ApiResponse<ExerciseResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ExerciseResponse> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        bindData(apiResponse.getResult());
                    } else {
                        showError("Không tìm thấy bài tập");
                    }
                } else {
                    showError("Lỗi tải dữ liệu: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExerciseResponse>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "API call failed", t);
                showError("Không thể kết nối đến server");
            }
        });
    }

    private void bindData(ExerciseResponse exercise) {
        // Tên bài tập
        tvExerciseName.setText(exercise.getExerciseName());

        // Độ khó
        String difficulty = mapDifficulty(exercise.getDifficultyLevel());
        tvDifficulty.setText(difficulty);

        // Calories
        tvCalories.setText(String.format("%.1f", exercise.getCaloriesPerMinute()));

        // Nhóm cơ
        tvMuscleGroups.setText(exercise.getMuscleGroups() != null ? 
                exercise.getMuscleGroups() : "Chưa xác định");

        // Thiết bị
        tvEquipment.setText(exercise.getEquipmentNeeded() != null ? 
                exercise.getEquipmentNeeded() : "Không cần");

        // Mô tả
        tvDescription.setText(exercise.getDescription() != null ? 
                exercise.getDescription() : "Chưa có mô tả");

        // Hướng dẫn
        tvInstructions.setText(exercise.getInstructions() != null ? 
                exercise.getInstructions() : "Chưa có hướng dẫn");

        // Danh mục
        if (exercise.getCategory() != null) {
            tvCategory.setText(exercise.getCategory().getCategoryName());
        } else {
            tvCategory.setText("Chưa phân loại");
        }

        // Load ảnh
        String imageUrl = exercise.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_pushup)
                    .error(R.drawable.img_pushup)
                    .centerCrop()
                    .into(ivExerciseImage);
        }
    }

    private String mapDifficulty(String level) {
        if (level == null) return "Dễ";
        switch (level.toUpperCase()) {
            case "BEGINNER": return "Dễ";
            case "INTERMEDIATE": return "Trung bình";
            case "ADVANCED": return "Khó";
            default: return level;
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupNavigation() {
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        LinearLayout homeNav = findViewById(R.id.navHome);
        LinearLayout workoutNav = findViewById(R.id.navWorkout);
        LinearLayout plannerNav = findViewById(R.id.navPlanner);
        LinearLayout profileNav = findViewById(R.id.navProfile);

        homeNav.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        workoutNav.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        plannerNav.setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        profileNav.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Button startWorkoutButton = findViewById(R.id.btnStartWorkout);
        startWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutTrainingActivity.class);
            intent.putExtra("exerciseId", exerciseId);
            startActivity(intent);
        });
    }
}
