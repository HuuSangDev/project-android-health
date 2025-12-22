package com.example.app_selfcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Adapter.FoodPagerAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.Fragment.IngredientsFragment;
import com.example.app_selfcare.Fragment.StepsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

        backButton = findViewById(R.id.backButton);
        ivRecipeImage = findViewById(R.id.iv_recipe_image);
        tvRecipeTitle = findViewById(R.id.tv_recipe_title);
        tvRecipeTime = findViewById(R.id.tv_recipe_time);
        saveButton = findViewById(R.id.saveButton);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        foodId = getIntent().getIntExtra("foodId", -1);

        initApiService();

        if (foodId != -1) {
            updateSaveIcon(isSaved(foodId));
            fetchFoodDetail();
        }

        backButton.setOnClickListener(v -> onBackPressed());

        saveButton.setOnClickListener(v -> toggleSaveFood(foodId));
    }

    // ================== API ==================
    private void initApiService() {
        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
    }

    private void fetchFoodDetail() {
        apiService.getFoodById(foodId).enqueue(new Callback<ApiResponse<FoodResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodResponse>> call,
                                   Response<ApiResponse<FoodResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayFoodDetail(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodResponse>> call, Throwable t) {
                Toast.makeText(RecipeDetailActivity.this,
                        "Lỗi tải chi tiết món ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayFoodDetail(FoodResponse food) {
        tvRecipeTitle.setText(food.getFoodName());
        tvRecipeTime.setText("⏰ " + (food.getPrepTime() + food.getCookTime()) + " phút");

        Glide.with(this)
                .load(food.getImageUrl())
                .placeholder(R.drawable.ic_platter_background)
                .into(ivRecipeImage);

        setupViewPager(food);
    }

    private void setupViewPager(FoodResponse food) {
        ArrayList<androidx.fragment.app.Fragment> fragments = new ArrayList<>();
        fragments.add(IngredientsFragment.newInstance(food));
        fragments.add(StepsFragment.newInstance(food));

        FoodPagerAdapter adapter = new FoodPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Thành phần" : "Quy trình")
        ).attach();
    }

    // ================== SAVE LOGIC ==================
    private boolean isSaved(int foodId) {
        SharedPreferences prefs = getSharedPreferences("SavedFoods", MODE_PRIVATE);
        return prefs.getBoolean(String.valueOf(foodId), false);
    }

    private void toggleSaveFood(int foodId) {
        SharedPreferences prefs = getSharedPreferences("SavedFoods", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean saved = prefs.getBoolean(String.valueOf(foodId), false);

        if (saved) {
            editor.remove(String.valueOf(foodId));
            Toast.makeText(this, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
        } else {
            editor.putBoolean(String.valueOf(foodId), true);
            Toast.makeText(this, "Đã lưu món ăn", Toast.LENGTH_SHORT).show();
        }

        editor.apply();
        updateSaveIcon(!saved);
    }

    private void updateSaveIcon(boolean saved) {
        saveButton.setImageResource(
                saved ? R.drawable.ic_saved : R.drawable.ic_save
        );
    }
    private void saveFood(Food food) {
        SharedPreferences prefs = getSharedPreferences("SAVED_FOODS", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString("foods", "");
        Type type = new TypeToken<List<Food>>(){}.getType();

        List<Food> savedFoods = json.isEmpty()
                ? new ArrayList<>()
                : gson.fromJson(json, type);

        // tránh lưu trùng
        for (Food f : savedFoods) {
            if (f.getId().equals(food.getId())) {
                Toast.makeText(this, "Món ăn đã được lưu trước đó", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        savedFoods.add(food);

        prefs.edit()
                .putString("foods", gson.toJson(savedFoods))
                .apply();

        Toast.makeText(this, "Đã lưu món ăn", Toast.LENGTH_SHORT).show();
    }

}
