package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.AdminFoodAdapter;
import com.example.app_selfcare.Data.Model.Request.FoodSearchRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCategoryResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.Model.Response.PageResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminFoodManagementActivity extends AppCompatActivity 
        implements AdminFoodAdapter.OnFoodClickListener {

    private static final String TAG = "AdminFoodManagement";

    private RecyclerView rvFoods;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ChipGroup chipGroupCategories;
    private FloatingActionButton fabAdd;

    private AdminFoodAdapter adapter;
    private ApiService apiService;

    private List<FoodCategoryResponse> categoryList = new ArrayList<>();
    private Long selectedCategoryId = null;

    // Launcher để nhận kết quả từ AddFoodActivity
    private final ActivityResultLauncher<Intent> addFoodLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoods(); // Refresh danh sách
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_food_management);

        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);

        initViews();
        setupRecyclerView();
        setupClickListeners();

        loadCategories();
        loadFoods();
    }

    private void initViews() {
        rvFoods = findViewById(R.id.rvFoods);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        fabAdd = findViewById(R.id.fabAdd);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AdminFoodAdapter(this);
        rvFoods.setLayoutManager(new LinearLayoutManager(this));
        rvFoods.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddFoodActivity.class);
            addFoodLauncher.launch(intent);
        });
    }

    private void loadCategories() {
        apiService.getAllFoodCategories().enqueue(new Callback<ApiResponse<List<FoodCategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodCategoryResponse>>> call,
                                   Response<ApiResponse<List<FoodCategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null 
                        && response.body().getResult() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getResult());
                    setupCategoryChips();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodCategoryResponse>>> call, Throwable t) {
                Log.e(TAG, "Load categories failed", t);
            }
        });
    }

    private void setupCategoryChips() {
        chipGroupCategories.removeAllViews();

        // Chip "Tất cả"
        Chip chipAll = new Chip(this);
        chipAll.setText("Tất cả");
        chipAll.setCheckable(true);
        chipAll.setChecked(true);
        chipAll.setOnClickListener(v -> {
            selectedCategoryId = null;
            loadFoods();
        });
        chipGroupCategories.addView(chipAll);

        // Chips cho từng category
        for (FoodCategoryResponse category : categoryList) {
            Chip chip = new Chip(this);
            chip.setText(category.getCategoryName());
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategoryId = category.getCategoryId();
                loadFoods();
            });
            chipGroupCategories.addView(chip);
        }
    }

    private void loadFoods() {
        showLoading(true);

        FoodSearchRequest request = FoodSearchRequest.builder()
                .categoryId(selectedCategoryId)
                .size(100)
                .build();

        apiService.searchFoods(request).enqueue(new Callback<ApiResponse<PageResponse<FoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<FoodResponse>>> call,
                                   Response<ApiResponse<PageResponse<FoodResponse>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null 
                        && response.body().getResult() != null) {
                    List<FoodResponse> foods = response.body().getResult().getContent();
                    adapter.setData(foods);

                    if (foods == null || foods.isEmpty()) {
                        showEmpty(true);
                    } else {
                        showEmpty(false);
                    }
                } else {
                    Log.e(TAG, "Load foods failed: " + response.code());
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<FoodResponse>>> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                Log.e(TAG, "Load foods error", t);
                Toast.makeText(AdminFoodManagementActivity.this, 
                        "Lỗi tải danh sách", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        rvFoods.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    // ==================== OnFoodClickListener ====================

    @Override
    public void onFoodClick(FoodResponse food) {
        Intent intent = new Intent(this, FoodDetailActivity.class);
        intent.putExtra("foodId", food.getFoodId());
        addFoodLauncher.launch(intent);
    }
}
