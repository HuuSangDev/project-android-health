package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.SavedFoodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.SavedFoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedFoodActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedFoodAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ApiService apiService;
    private final List<SavedFoodResponse> savedFoodList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        initViews();
        initApiService();
        setupRecyclerView();
        setupClickListeners();
        loadSavedFoods();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerSavedFood);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
    }

    private void initApiService() {
        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SavedFoodAdapter(savedFoodList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navWorkout).setOnClickListener(v ->
                startActivity(new Intent(this, WorkoutActivity.class)));

        findViewById(R.id.navPlanner).setOnClickListener(v ->
                startActivity(new Intent(this, RecipeHomeActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadSavedFoods() {
        showLoading(true);
        
        apiService.getMySavedFoods().enqueue(new Callback<ApiResponse<List<SavedFoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SavedFoodResponse>>> call, 
                                 Response<ApiResponse<List<SavedFoodResponse>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<SavedFoodResponse> savedFoods = response.body().getResult();
                    updateSavedFoodsList(savedFoods);
                } else {
                    Toast.makeText(SavedFoodActivity.this, "Lỗi tải danh sách món ăn đã lưu", Toast.LENGTH_SHORT).show();
                    showEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SavedFoodResponse>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(SavedFoodActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                showEmptyState(true);
            }
        });
    }

    private void updateSavedFoodsList(List<SavedFoodResponse> savedFoods) {
        savedFoodList.clear();
        savedFoodList.addAll(savedFoods);
        adapter.notifyDataSetChanged();
        
        showEmptyState(savedFoods.isEmpty());
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload khi quay lại activity để cập nhật danh sách
        loadSavedFoods();
    }
}
