package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {

    private static final String TAG = "FoodDetailActivity";

    private ImageView ivFoodImage, btnEdit, btnDelete;
    private TextView tvFoodName, tvCalories, tvProtein, tvFat, tvFiber, tvSugar;
    private TextView tvMealType, tvDifficulty, tvCategory, tvInstructions;
    private TextView tvPrepTime, tvCookTime, tvServings;
    private ProgressBar progressBar;
    private View contentLayout;

    private ApiService apiService;
    private int foodId = -1;
    private FoodResponse currentFood;

    private final ActivityResultLauncher<Intent> editFoodLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoodDetail(foodId); // Reload after edit
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);

        initViews();
        setupClickListeners();

        foodId = getIntent().getIntExtra("foodId", -1);
        if (foodId != -1) {
            loadFoodDetail(foodId);
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        ivFoodImage = findViewById(R.id.ivFoodImage);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvCalories = findViewById(R.id.tvCalories);
        tvProtein = findViewById(R.id.tvProtein);
        tvFat = findViewById(R.id.tvFat);
        tvFiber = findViewById(R.id.tvFiber);
        tvSugar = findViewById(R.id.tvSugar);
        tvMealType = findViewById(R.id.tvMealType);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvCategory = findViewById(R.id.tvCategory);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvPrepTime = findViewById(R.id.tvPrepTime);
        tvCookTime = findViewById(R.id.tvCookTime);
        tvServings = findViewById(R.id.tvServings);
        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void setupClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            if (currentFood != null) {
                Intent intent = new Intent(this, AddFoodActivity.class);
                intent.putExtra("food", currentFood);
                editFoodLauncher.launch(intent);
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (currentFood != null) {
                showDeleteConfirmDialog();
            }
        });
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa món \"" + currentFood.getFoodName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteFood())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteFood() {
        showLoading(true);

        apiService.deleteFood(foodId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(FoodDetailActivity.this, "Đã xóa món ăn", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Lỗi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(FoodDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFoodDetail(int id) {
        showLoading(true);

        apiService.getFoodById(id).enqueue(new Callback<ApiResponse<FoodResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodResponse>> call,
                                   Response<ApiResponse<FoodResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null 
                        && response.body().getResult() != null) {
                    currentFood = response.body().getResult();
                    bindData(currentFood);
                } else {
                    Toast.makeText(FoodDetailActivity.this, 
                            "Không tải được chi tiết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodResponse>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Load food detail failed", t);
                Toast.makeText(FoodDetailActivity.this, 
                        "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(FoodResponse food) {
        tvFoodName.setText(food.getFoodName());
        tvCalories.setText(String.format("%.0f", food.getCaloriesPer100g()));
        tvProtein.setText(String.format("%.1f g", food.getProteinPer100g()));
        tvFat.setText(String.format("%.1f g", food.getFatPer100g()));
        tvFiber.setText(String.format("%.1f g", food.getFiberPer100g()));
        tvSugar.setText(String.format("%.1f g", food.getSugarPer100g()));

        tvMealType.setText(mapMealType(food.getMealType()));
        tvDifficulty.setText(mapDifficulty(food.getDifficultyLevel()));

        if (food.getCategoryResponse() != null) {
            tvCategory.setText(food.getCategoryResponse().getCategoryName());
        } else {
            tvCategory.setText("Chưa phân loại");
        }

        tvInstructions.setText(food.getInstructions() != null ? 
                food.getInstructions() : "Chưa có hướng dẫn");

        tvPrepTime.setText(food.getPrepTime() + " phút");
        tvCookTime.setText(food.getCookTime() + " phút");
        tvServings.setText(food.getServings() + " người");

        // Load image
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .centerCrop()
                    .into(ivFoodImage);
        }
    }

    private String mapMealType(String mealType) {
        if (mealType == null) return "Không xác định";
        switch (mealType.toUpperCase()) {
            case "BREAKFAST": return "Bữa sáng";
            case "LUNCH": return "Bữa trưa";
            case "DINNER": return "Bữa tối";
            case "ALL": return "Tất cả bữa";
            default: return mealType;
        }
    }

    private String mapDifficulty(String difficulty) {
        if (difficulty == null) return "Dễ";
        switch (difficulty.toUpperCase()) {
            case "EASY": return "Dễ";
            case "MEDIUM": return "Trung bình";
            case "HARD": return "Khó";
            default: return difficulty;
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
