package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class WorkoutActivity extends AppCompatActivity {

    private RecyclerView rvExercises;
    private ExerciseAdapter exerciseAdapter;
    private final List<Exercise> exerciseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout);

        // Fix padding cho status bar + navigation bar (dính đáy đẹp mọi máy)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            findViewById(R.id.bottomNav).setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        // ==================== BOTTOM NAVIGATION (dùng ID mới) ====================
        View navHome     = findViewById(R.id.navHome);
        View navWorkout  = findViewById(R.id.navWorkout);
        View navPlanner  = findViewById(R.id.navPlanner);
        View navProfile  = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        navWorkout.setOnClickListener(v -> {
            // Đã ở màn Workout
        });

        navPlanner.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutActivity.this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutActivity.this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // ==================== NÚT BACK ====================
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // ==================== RecyclerView Bài tập ====================
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        exerciseList.clear();
        exerciseAdapter = new ExerciseAdapter(exerciseList);
        rvExercises.setAdapter(exerciseAdapter);

        loadExercisesFromApi();
    }

    private void loadExercisesFromApi() {
        ApiService apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
        Call<ApiResponse<List<ExerciseResponse>>> call = apiService.getExercises();

        call.enqueue(new Callback<ApiResponse<List<ExerciseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExerciseResponse>>> call,
                                   Response<ApiResponse<List<ExerciseResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    exerciseList.clear();
                    for (ExerciseResponse exRes : response.body().getResult()) {
                        exerciseList.add(exRes.toExercise());
                    }
                    exerciseAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(WorkoutActivity.this,
                            "Không tải được danh sách bài tập", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExerciseResponse>>> call, Throwable t) {
                android.util.Log.e("WorkoutActivity", "Load exercises failed", t);
                Toast.makeText(WorkoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}