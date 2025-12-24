package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.utils.ImageUtils;
import com.example.app_selfcare.utils.LocaleManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodIngredientsActivity extends AppCompatActivity {

    private static final String TAG = "FoodIngredients";
    
    private ImageView btnBack, foodImage;
    private TextView tvFoodName, tvCalories, tvProtein, tvFat, tvFiber, tvSugar;
    private TextView tvPrepTime, tvCookTime, tvServings, tvDifficulty, tvMealType, tvGoal;
    private TextView tvInstructions, tvCategory;
    private ProgressBar progressBar;
    private CardView cardNutrition, cardDetails, cardInstructions;
    
    private Long foodId;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_ingredients);

        // Lấy foodId từ Intent
        foodId = getIntent().getLongExtra("FOOD_ID", -1);
        if (foodId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupEvents();
        fetchFoodDetails();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        foodImage = findViewById(R.id.foodImage);
        tvFoodName = findViewById(R.id.tvFoodName);
        
        // Nutrition info
        tvCalories = findViewById(R.id.tvCalories);
        tvProtein = findViewById(R.id.tvProtein);
        tvFat = findViewById(R.id.tvFat);
        tvFiber = findViewById(R.id.tvFiber);
        tvSugar = findViewById(R.id.tvSugar);
        
        // Details
        tvPrepTime = findViewById(R.id.tvPrepTime);
        tvCookTime = findViewById(R.id.tvCookTime);
        tvServings = findViewById(R.id.tvServings);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvMealType = findViewById(R.id.tvMealType);
        tvGoal = findViewById(R.id.tvGoal);
        tvCategory = findViewById(R.id.tvCategory);
        
        // Instructions
        tvInstructions = findViewById(R.id.tvInstructions);
        
        // Cards and progress
        cardNutrition = findViewById(R.id.cardNutrition);
        cardDetails = findViewById(R.id.cardDetails);
        cardInstructions = findViewById(R.id.cardInstructions);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        
        // Click vào ảnh để xem phóng to
        foodImage.setOnClickListener(v -> showImageDetail());
    }

    private void showImageDetail() {
        // Tạm thời comment out vì chưa có ImageDetailActivity
        // FoodResponse food = (FoodResponse) foodImage.getTag();
        // if (food != null && food.getImageUrl() != null) {
        //     Intent intent = new Intent(this, ImageDetailActivity.class);
        //     intent.putExtra("IMAGE_URL", food.getImageUrl());
        //     intent.putExtra("TITLE", food.getFoodName());
        //     startActivity(intent);
        // }
        Toast.makeText(this, "Tính năng xem ảnh chi tiết sẽ được cập nhật", Toast.LENGTH_SHORT).show();
    }

    private void fetchFoodDetails() {
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        hideAllCards();

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getFoodById(foodId.intValue(), "Bearer " + token).enqueue(new Callback<ApiResponse<FoodResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodResponse>> call, 
                                 Response<ApiResponse<FoodResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    Toast.makeText(FoodIngredientsActivity.this, "Không thể tải thông tin món ăn", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                FoodResponse food = response.body().getResult();
                displayFoodDetails(food);
                showAllCards();
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error fetching food details", t);
                Toast.makeText(FoodIngredientsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayFoodDetails(FoodResponse food) {
        // Lưu food object vào tag của image để sử dụng khi click
        foodImage.setTag(food);
        
        // Tên món ăn
        tvFoodName.setText(food.getFoodName() != null ? food.getFoodName() : "Không có tên");
        
        // Load ảnh
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            ImageUtils.loadImageSafely(this, food.getImageUrl(), foodImage);
        } else {
            foodImage.setImageResource(R.drawable.ic_proflie);
        }
        
        // Thông tin dinh dưỡng (per 100g)
        tvCalories.setText(String.format("%.1f kcal", food.getCaloriesPer100g()));
        tvProtein.setText(String.format("%.1f g", food.getProteinPer100g()));
        tvFat.setText(String.format("%.1f g", food.getFatPer100g()));
        tvFiber.setText(String.format("%.1f g", food.getFiberPer100g()));
        tvSugar.setText(String.format("%.1f g", food.getSugarPer100g()));
        
        // Chi tiết món ăn
        tvPrepTime.setText(String.format("%d phút", food.getPrepTime()));
        tvCookTime.setText(String.format("%d phút", food.getCookTime()));
        tvServings.setText(String.format("%d người", food.getServings()));
        tvDifficulty.setText(food.getDifficultyLevel() != null ? food.getDifficultyLevel() : "Không xác định");
        tvMealType.setText(food.getMealType() != null ? food.getMealType() : "Không xác định");
        tvGoal.setText(food.getGoal() != null ? food.getGoal() : "Không xác định");
        
        // Category
        if (food.getCategoryResponse() != null && food.getCategoryResponse().getCategoryName() != null) {
            tvCategory.setText(food.getCategoryResponse().getCategoryName());
        } else {
            tvCategory.setText("Không có danh mục");
        }
        
        // Hướng dẫn
        if (food.getInstructions() != null && !food.getInstructions().isEmpty()) {
            tvInstructions.setText(food.getInstructions());
        } else {
            tvInstructions.setText("Không có hướng dẫn");
        }
    }

    private void hideAllCards() {
        cardNutrition.setVisibility(View.GONE);
        cardDetails.setVisibility(View.GONE);
        cardInstructions.setVisibility(View.GONE);
    }

    private void showAllCards() {
        cardNutrition.setVisibility(View.VISIBLE);
        cardDetails.setVisibility(View.VISIBLE);
        cardInstructions.setVisibility(View.VISIBLE);
    }
}