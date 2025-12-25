package com.example.app_selfcare;

import android.content.Context;
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
import com.example.app_selfcare.utils.LocaleManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutDetailActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutDetailActivity";

    private ImageView ivExerciseImage;
    private TextView tvExerciseName, tvDifficulty, tvCalories;
    private TextView tvDescription, tvInstructions;
    private ProgressBar progressBar;
    private View scrollView;
    private Button btnStartWorkout;

    private ApiService apiService;
    private int exerciseId = -1;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

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

        exerciseId = getIntent().getIntExtra("exerciseId", -1);
        if (exerciseId != -1) {
            loadExerciseDetail(exerciseId);
        }
    }

    private void initViews() {
        ivExerciseImage = findViewById(R.id.ivExerciseImage);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvCalories = findViewById(R.id.tvCalories);
        tvDescription = findViewById(R.id.tvDescription);
        tvInstructions = findViewById(R.id.tvInstructions);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);
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
                    ExerciseResponse exercise = response.body().getResult();
                    if (exercise != null) {
                        bindData(exercise);
                    } else {
                        showError("Không tìm thấy bài tập");
                    }
                } else {
                    showError("Lỗi tải dữ liệu");
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
        tvExerciseName.setText(exercise.getExerciseName());
        tvDifficulty.setText(mapDifficulty(exercise.getDifficultyLevel()));
        tvCalories.setText(String.format("%.1f cal/phút", exercise.getCaloriesPerMinute()));
        tvDescription.setText(exercise.getDescription() != null ? exercise.getDescription() : "Chưa có mô tả");
        tvInstructions.setText(exercise.getInstructions() != null ? exercise.getInstructions() : "Chưa có hướng dẫn");

        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(exercise.getImageUrl())
                    .placeholder(R.drawable.img_pushup)
                    .error(R.drawable.img_pushup)
                    .centerCrop()
                    .into(ivExerciseImage);
        }

        btnStartWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutDetailActivity.this, WorkoutTrainingActivity.class);
            intent.putExtra("exerciseId", exerciseId);
            intent.putExtra("exerciseName", exercise.getExerciseName());
            intent.putExtra("caloriesPerMinute", exercise.getCaloriesPerMinute());
            startActivity(intent);
        });
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
        backButton.setOnClickListener(v -> finish());

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
    }
}
