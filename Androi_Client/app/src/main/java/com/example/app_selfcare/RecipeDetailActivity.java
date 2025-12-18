package com.example.app_selfcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Adapter.FoodPagerAdapter;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.Fragment.IngredientsFragment;
import com.example.app_selfcare.Fragment.StepsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView backButton, ivRecipeImage, saveButton;
    private TextView tvRecipeTitle, tvRecipeTime;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ApiService apiService;
    private int foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Khởi tạo views
        backButton = findViewById(R.id.backButton);
        ivRecipeImage = findViewById(R.id.iv_recipe_image);
        tvRecipeTitle = findViewById(R.id.tv_recipe_title);
        tvRecipeTime = findViewById(R.id.tv_recipe_time);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        saveButton = findViewById(R.id.saveButton);

        ImageView homeIcon = findViewById(R.id.navHome).findViewById(R.id.homeIcon); // Nếu layout có ID này, hoặc dùng ID của LinearLayout
        // Tuy nhiên, theo XML cũ, các ID là navHome, navWorkout... cho LinearLayout
        View navHome = findViewById(R.id.navHome);
        View navWorkout = findViewById(R.id.navWorkout);
        View navPlanner = findViewById(R.id.navPlanner);
        View navProfile = findViewById(R.id.navProfile);

        // Nhận dữ liệu từ Intent
        foodId = getIntent().getIntExtra("foodId", -1);
        String recipeName = getIntent().getStringExtra("foodName");

        // Cập nhật giao diện sơ bộ từ intent nếu có
        if (recipeName != null) tvRecipeTitle.setText(recipeName);

        initApiService();
        if (foodId != -1) {
            fetchFoodDetail();
        } else {
            Toast.makeText(this, "Không tìm thấy ID món ăn", Toast.LENGTH_SHORT).show();
        }

        // Xử lý click back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Xử lý click save button
        saveButton.setOnClickListener(v -> {
            String recipeNameToSave = tvRecipeTitle.getText().toString();
            String recipeTimeToSave = tvRecipeTime.getText().toString().replace("⏰ ", "");
            saveRecipe(recipeNameToSave, recipeTimeToSave);
            Toast.makeText(this, "Đã lưu món: " + recipeNameToSave, Toast.LENGTH_SHORT).show();
        });

        // Bottom navigation
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeDetailActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        navWorkout.setOnClickListener(v -> {
            startActivity(new Intent(RecipeDetailActivity.this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        navPlanner.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeDetailActivity.this, RecipeHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(RecipeDetailActivity.this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void initApiService() {
        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
    }

    private void fetchFoodDetail() {
        apiService.getFoodById(foodId).enqueue(new Callback<ApiResponse<FoodResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodResponse>> call, Response<ApiResponse<FoodResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FoodResponse food = response.body().getResult();
                    if (food != null) {
                        displayFoodDetail(food);
                    }
                } else {
                    Log.e("RecipeDetail", "Error: " + response.code());
                    Toast.makeText(RecipeDetailActivity.this, "Lỗi khi tải chi tiết món ăn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodResponse>> call, Throwable t) {
                Log.e("RecipeDetail", "Failure: " + t.getMessage());
                Toast.makeText(RecipeDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayFoodDetail(FoodResponse food) {
        tvRecipeTitle.setText(food.getFoodName());
        tvRecipeTime.setText("⏰ " + (food.getPrepTime() + food.getCookTime()) + " phút");

        // Load ảnh bằng Glide
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_platter_background)
                    .error(R.drawable.ic_platter_background)
                    .into(ivRecipeImage);
        }

        // Setup ViewPager với Fragments và truyền dữ liệu qua Bundle
        setupViewPager(food);
    }

    private void setupViewPager(FoodResponse food) {
        ArrayList<androidx.fragment.app.Fragment> fragments = new ArrayList<>();
        fragments.add(IngredientsFragment.newInstance(food));
        fragments.add(StepsFragment.newInstance(food));

        FoodPagerAdapter adapter = new FoodPagerAdapter(this, fragments);
        viewPager.setOffscreenPageLimit(2); // Giữ cả 2 fragment trong bộ nhớ
        viewPager.setAdapter(adapter);

        // Re-attach TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Thành phần");
                    break;
                case 1:
                    tab.setText("Quy trình");
                    break;
            }
        }).attach();
    }

    private String convertDifficulty(String level) {
        if (level == null) return "Dễ";
        switch (level.toUpperCase()) {
            case "EASY": return "Dễ";
            case "MEDIUM": return "Trung bình";
            case "HARD": return "Khó";
            default: return level;
        }
    }

    // Hàm lưu món ăn (sử dụng SharedPreferences)
    private void saveRecipe(String name, String time) {
        SharedPreferences prefs = getSharedPreferences("SavedRecipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String savedRecipes = prefs.getString("recipes", "");
        if (!savedRecipes.contains(name)) {
            savedRecipes += name + "|" + time + ";";
            editor.putString("recipes", savedRecipes);
            editor.apply();
        }
    }
}