package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.ExerciseAdapter;
import com.example.app_selfcare.Data.Model.Exercise;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutActivity extends BaseActivity {

    private RecyclerView rvExercises;
    private ExerciseAdapter exerciseAdapter;
    private final List<Exercise> exerciseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // ===== Fix padding cho status bar + bottom nav =====
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            findViewById(R.id.bottomNav).setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        initBottomNavigation();
        initRecyclerView();
        initBackButton();

        // 👉 LOAD API THẬT + LOADING CHUNG
        loadExercisesFromApi();
    }

    // ================= BOTTOM NAV =================
    private void initBottomNavigation() {
        View navHome    = findViewById(R.id.navHome);
        View navWorkout = findViewById(R.id.navWorkout);
        View navPlanner = findViewById(R.id.navPlanner);
        View navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        navWorkout.setOnClickListener(v -> {
            // Đang ở trang Workout → không làm gì
        });

        navPlanner.setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    // ================= BACK BUTTON =================
    private void initBackButton() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    // ================= RECYCLER VIEW =================
    private void initRecyclerView() {
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));

        exerciseAdapter = new ExerciseAdapter(exerciseList);

        exerciseAdapter.setOnItemClickListener(exercise -> {
            Intent intent = new Intent(this, WorkoutDetailActivity.class);
            intent.putExtra("exerciseId", exercise.getId());
            intent.putExtra("exerciseName", exercise.getName());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        rvExercises.setAdapter(exerciseAdapter);
    }

    // ================= API =================
    private void loadExercisesFromApi() {

        // 👉 HIỆN LOADING CHUNG (Lottie)
        showLoading();

        ApiService apiService = ApiClient
                .getClientWithToken(this)
                .create(ApiService.class);

        Call<ApiResponse<List<ExerciseResponse>>> call = apiService.getExercises();

        call.enqueue(new Callback<ApiResponse<List<ExerciseResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<ExerciseResponse>>> call,
                    Response<ApiResponse<List<ExerciseResponse>>> response
            ) {
                hideLoading();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResult() != null) {

                    exerciseList.clear();

                    for (ExerciseResponse res : response.body().getResult()) {
                        exerciseList.add(res.toExercise());
                    }

                    exerciseAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(
                            WorkoutActivity.this,
                            "Không tải được danh sách bài tập",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<List<ExerciseResponse>>> call,
                    Throwable t
            ) {
                hideLoading();
                Toast.makeText(
                        WorkoutActivity.this,
                        "Lỗi kết nối server",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
