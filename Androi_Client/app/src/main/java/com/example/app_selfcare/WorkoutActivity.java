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
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.Adapter.ExerciseAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutActivity extends AppCompatActivity {

    private ExerciseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout);

        // Fix padding cho status bar + navigation bar (dính đáy đẹp mọi máy)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            // Bottom nav tự động dính đáy nhờ padding bottom ở XML + insets
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
            // Đã ở màn Workout rồi → không cần chuyển, chỉ refresh nếu muốn
            // (hoặc bỏ qua để tránh reload không cần thiết)
        });

        navPlanner.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutActivity.this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(WorkoutActivity.this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // ==================== NÚT BACK + CHI TIẾT BÀI TẬP ====================
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        setupRecycler();
        fetchExercises();

        // (Tùy chọn) Các card khác nếu muốn click được
        findViewById(R.id.cardHeader).setOnClickListener(v -> {
            // Có thể mở danh sách toàn bộ Strength workouts
        });
    }

    private void setupRecycler() {
        RecyclerView rv = findViewById(R.id.rvExercises);
        adapter = new ExerciseAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void fetchExercises() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllExercises().enqueue(new Callback<ApiResponse<List<ExerciseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExerciseResponse>>> call, Response<ApiResponse<List<ExerciseResponse>>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(WorkoutActivity.this, "Không tải được danh sách", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<ExerciseResponse> list = response.body().getResult();
                adapter.submit(list);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExerciseResponse>>> call, Throwable t) {
                Toast.makeText(WorkoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}