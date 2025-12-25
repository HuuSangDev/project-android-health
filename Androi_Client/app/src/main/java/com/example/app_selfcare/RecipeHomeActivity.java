package com.example.app_selfcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.UserProfileResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.Fragment.FoodPeriodFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeHomeActivity extends BaseActivity {

    private View navHome, navWorkout, navPlanner, navProfile;
    private TextView tvAll, tvBreakfast, tvLunch, tvDinner, tvSaved;
    private TextView tvHelloName;
    private ImageView ivUserAvatar;

    private String currentMealType = "ALL";

    private Fragment allFragment, breakfastFragment, lunchFragment, dinnerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_home);

        initViews();
        setupClickListeners();
        loadUserProfile();

        loadFragment("ALL");
        updateTabSelection(tvAll);
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navWorkout = findViewById(R.id.navWorkout);
        navPlanner = findViewById(R.id.navPlanner);
        navProfile = findViewById(R.id.navProfile);

        tvAll = findViewById(R.id.tvAll);
        tvBreakfast = findViewById(R.id.tvBreakfast);
        tvLunch = findViewById(R.id.tvLunch);
        tvDinner = findViewById(R.id.tvDinner);
        tvSaved = findViewById(R.id.tvSaved);
        
        tvHelloName = findViewById(R.id.tvHelloName);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
    }

    private void setupClickListeners() {
        tvAll.setOnClickListener(v -> switchTab("ALL", tvAll));
        tvBreakfast.setOnClickListener(v -> switchTab("BREAKFAST", tvBreakfast));
        tvLunch.setOnClickListener(v -> switchTab("LUNCH", tvLunch));
        tvDinner.setOnClickListener(v -> switchTab("DINNER", tvDinner));

        tvSaved.setOnClickListener(v ->
                startActivity(new Intent(this, SavedFoodActivity.class))
        );

        navHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        navWorkout.setOnClickListener(v -> startActivity(new Intent(this, WorkoutActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void switchTab(String type, TextView tab) {
        if (!currentMealType.equals(type)) {
            loadFragment(type);
            updateTabSelection(tab);
        }
    }

    private void loadFragment(String type) {
        currentMealType = type;

        Fragment fragment;
        switch (type) {
            case "BREAKFAST":
                if (breakfastFragment == null)
                    breakfastFragment = FoodPeriodFragment.newInstance("BREAKFAST");
                fragment = breakfastFragment;
                break;
            case "LUNCH":
                if (lunchFragment == null)
                    lunchFragment = FoodPeriodFragment.newInstance("LUNCH");
                fragment = lunchFragment;
                break;
            case "DINNER":
                if (dinnerFragment == null)
                    dinnerFragment = FoodPeriodFragment.newInstance("DINNER");
                fragment = dinnerFragment;
                break;
            default:
                if (allFragment == null)
                    allFragment = FoodPeriodFragment.newInstance("ALL");
                fragment = allFragment;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void updateTabSelection(TextView selected) {
        resetTab(tvAll);
        resetTab(tvBreakfast);
        resetTab(tvLunch);
        resetTab(tvDinner);

        selected.setBackgroundResource(R.drawable.bg_chip_selected);
        selected.setTextColor(Color.WHITE);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_chip_unselected);
        tab.setTextColor(Color.parseColor("#0F955A"));
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Log.w("RecipeHomeActivity", "No token found");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getUserProfile("Bearer " + token).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    UserResponse user = response.body().getResult();
                    updateUserInfo(user);
                } else {
                    Log.w("RecipeHomeActivity", "Failed to load user profile");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e("RecipeHomeActivity", "Error loading user profile: " + t.getMessage());
            }
        });
    }

    private void updateUserInfo(UserResponse user) {
        // Cập nhật tên người dùng
        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            tvHelloName.setText("Xin chào, " + user.getFullName());
        }

        // Cập nhật avatar
        UserProfileResponse profile = user.getUserProfileResponse();
        if (profile != null && profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.ic_proflie)
                    .error(R.drawable.ic_proflie)
                    .circleCrop()
                    .into(ivUserAvatar);
        }
    }
}
