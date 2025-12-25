package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class ExercisesByCategoryActivity extends AppCompatActivity {

    private static final String TAG = "ExercisesByCategoryActivity";

    private RecyclerView recyclerView;
    private View layoutLoading;
    private View layoutEmpty;
    private Toolbar toolbar;

    private ExerciseAdapter adapter;
    private ApiService apiService;
    private List<Exercise> exerciseList = new ArrayList<>();

    private long categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_by_category);

        // Lấy dữ liệu từ Intent
        categoryId = getIntent().getLongExtra("categoryId", -1);
        categoryName = getIntent().getStringExtra("categoryName");

        if (categoryId == -1) {
            Toast.makeText(this, "Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadExercises();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewExercises);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryName != null ? categoryName : "Danh mục");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new ExerciseAdapter(exerciseList);
        adapter.setOnItemClickListener(exercise -> {
            // Mở màn hình chi tiết bài tập
            Intent intent = new Intent(this, WorkoutDetailActivity.class);
            intent.putExtra("exerciseId", exercise.getId());
            intent.putExtra("exerciseName", exercise.getName());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadExercises() {
        showLoading(true);

        apiService.getExercisesByCategory(categoryId).enqueue(new Callback<ApiResponse<List<ExerciseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExerciseResponse>>> call,
                                   Response<ApiResponse<List<ExerciseResponse>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().getResult() != null) {
                    exerciseList.clear();
                    for (ExerciseResponse exerciseResponse : response.body().getResult()) {
                        exerciseList.add(exerciseResponse.toExercise());
                    }
                    adapter.notifyDataSetChanged();

                    showEmpty(exerciseList.isEmpty());
                } else {
                    Log.e(TAG, "Load exercises failed: " + response.code());
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExerciseResponse>>> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                Log.e(TAG, "Load exercises error", t);
                Toast.makeText(ExercisesByCategoryActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmpty(boolean show) {
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
