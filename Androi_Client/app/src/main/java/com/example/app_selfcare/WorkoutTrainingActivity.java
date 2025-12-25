package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class WorkoutTrainingActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutTrainingActivity";

    private ImageView imgExercise;
    private TextView tvExerciseName, tvCurrentProgress, tvInstruction;
    private Button btnCompleteWorkout;

    private ApiService apiService;
    private int exerciseId = -1;
    private String exerciseName = "";
    private double caloriesPerMinute = 0;

    private int currentSet = 1;
    private int totalSets = 3;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private int elapsedSeconds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_training);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initApiService();
        setupNavigation();
        loadExerciseData();
        startTimer();
    }

    private void initViews() {
        imgExercise = findViewById(R.id.imgExercise);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvCurrentProgress = findViewById(R.id.tvCurrentProgress);
        tvInstruction = findViewById(R.id.tvInstruction);
        btnCompleteWorkout = findViewById(R.id.btnCompleteWorkout);

        btnCompleteWorkout.setOnClickListener(v -> completeWorkout());
    }

    private void initApiService() {
        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
    }

    private void loadExerciseData() {
        exerciseId = getIntent().getIntExtra("exerciseId", -1);
        exerciseName = getIntent().getStringExtra("exerciseName");
        caloriesPerMinute = getIntent().getDoubleExtra("caloriesPerMinute", 0);

        if (exerciseId != -1) {
            loadExerciseDetail(exerciseId);
        }
    }

    private void loadExerciseDetail(int id) {
        apiService.getExerciseById(id).enqueue(new Callback<ApiResponse<ExerciseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExerciseResponse>> call,
                                   Response<ApiResponse<ExerciseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExerciseResponse exercise = response.body().getResult();
                    if (exercise != null) {
                        bindData(exercise);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExerciseResponse>> call, Throwable t) {
                Log.e(TAG, "Failed to load exercise", t);
            }
        });
    }

    private void bindData(ExerciseResponse exercise) {
        tvExerciseName.setText(exercise.getExerciseName());
        tvInstruction.setText(exercise.getInstructions() != null ? exercise.getInstructions() : "Không có hướng dẫn");
        updateProgress();

        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(exercise.getImageUrl())
                    .placeholder(R.drawable.img_pushup)
                    .error(R.drawable.img_pushup)
                    .centerCrop()
                    .into(imgExercise);
        }
    }

    private void updateProgress() {
        tvCurrentProgress.setText(String.format("Tiến độ hiện tại: %d/%d Hiệp", currentSet, totalSets));
    }

    private void startTimer() {
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedSeconds++;
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void completeWorkout() {
        stopTimer();

        if (currentSet < totalSets) {
            currentSet++;
            updateProgress();
            elapsedSeconds = 0;
            startTimer();
            Toast.makeText(this, "Hoàn thành hiệp " + (currentSet - 1), Toast.LENGTH_SHORT).show();
        } else {
            // All sets completed
            double caloriesBurned = (caloriesPerMinute * elapsedSeconds) / 60.0;
            Toast.makeText(this, String.format("Bài tập hoàn thành! Calo đốt: %.1f", caloriesBurned), Toast.LENGTH_SHORT).show();

            // Return to workout list
            Intent intent = new Intent(WorkoutTrainingActivity.this, WorkoutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void setupNavigation() {
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        LinearLayout homeNav = findViewById(R.id.navHome);
        LinearLayout workoutNav = findViewById(R.id.navWorkout);
        LinearLayout plannerNav = findViewById(R.id.navPlanner);
        LinearLayout profileNav = findViewById(R.id.navProfile);

        homeNav.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutTrainingActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        workoutNav.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutTrainingActivity.this, WorkoutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        plannerNav.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutTrainingActivity.this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        profileNav.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutTrainingActivity.this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}