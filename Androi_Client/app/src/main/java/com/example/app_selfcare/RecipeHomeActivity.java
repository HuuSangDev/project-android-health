package com.example.app_selfcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_selfcare.Fragment.FoodPeriodFragment;

public class RecipeHomeActivity extends AppCompatActivity {

    private View navHome, navWorkout, navPlanner, navProfile;
    private TextView tvAll, tvBreakfast, tvLunch, tvDinner;
    private Button btnSavedRecipes;

    private String currentMealType = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_home);

        initViews();
        setupClickListeners();

        // Load default fragment (All recipes)
        loadFragment("ALL");
        updateTabSelection(tvAll);
    }

    private void initViews() {
        // Bottom Navigation
        navHome = findViewById(R.id.navHome);
        navWorkout = findViewById(R.id.navWorkout);
        navPlanner = findViewById(R.id.navPlanner);
        navProfile = findViewById(R.id.navProfile);

        // Meal Type Tabs
        tvAll = findViewById(R.id.tvAll);
        tvBreakfast = findViewById(R.id.tvBreakfast);
        tvLunch = findViewById(R.id.tvLunch);
        tvDinner = findViewById(R.id.tvDinner);

//        btnSavedRecipes = findViewById(R.id.btn_saved_recipes);
    }

    private void setupClickListeners() {
        // Meal Type Tabs
        tvAll.setOnClickListener(v -> {
            if (!currentMealType.equals("ALL")) {
                loadFragment("ALL");
                updateTabSelection(tvAll);
            }
        });

        tvBreakfast.setOnClickListener(v -> {
            if (!currentMealType.equals("BREAKFAST")) {
                loadFragment("BREAKFAST");
                updateTabSelection(tvBreakfast);
            }
        });

        tvLunch.setOnClickListener(v -> {
            if (!currentMealType.equals("LUNCH")) {
                loadFragment("LUNCH");
                updateTabSelection(tvLunch);
            }
        });

        tvDinner.setOnClickListener(v -> {
            if (!currentMealType.equals("DINNER")) {
                loadFragment("DINNER");
                updateTabSelection(tvDinner);
            }
        });

//        // Saved Recipes Button
//        btnSavedRecipes.setOnClickListener(v -> {
//            startActivity(new Intent(RecipeHomeActivity.this, SavedRecipesActivity.class));
//        });

        // Bottom Navigation
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeHomeActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        navWorkout.setOnClickListener(v -> {
            startActivity(new Intent(RecipeHomeActivity.this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        navPlanner.setOnClickListener(v -> {
            // Already on this screen, do nothing or refresh
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(RecipeHomeActivity.this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loadFragment(String mealType) {
        // QUAN TRỌNG: Gán currentMealType SAU khi dùng để so sánh
        // → Lưu lại loại cũ trước
        String oldMealType = currentMealType;
        currentMealType = mealType;

        Fragment fragment = FoodPeriodFragment.newInstance(mealType);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Xác định hướng chuyển tab để chọn animation đúng
        if (oldMealType.equals("ALL") || mealType.equals("ALL")) {
            // Từ All → bất kỳ hoặc bất kỳ → All → luôn slide từ phải
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        } else {
            // So sánh thứ tự: ALL → BREAKFAST → LUNCH → DINNER
            int oldIndex = getMealIndex(oldMealType);
            int newIndex = getMealIndex(mealType);

            if (newIndex > oldIndex) {
                // Tiến (Sáng → Trưa, Trưa → Tối, v.v.)
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
            } else {
                // Lùi (Tối → Trưa, Trưa → Sáng, v.v.)
                transaction.setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
            }
        }

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
    private int getMealIndex(String type) {
        switch (type) {
            case "ALL":        return 0;
            case "BREAKFAST":  return 1;
            case "LUNCH":      return 2;
            case "DINNER":     return 3;
            default:           return 0;
        }
    }

    private boolean isMovingForward(String newMealType) {
        String[] order = {"ALL", "BREAKFAST", "LUNCH", "DINNER"};
        int oldIndex = -1, newIndex = -1;

        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(currentMealType)) oldIndex = i;
            if (order[i].equals(newMealType)) newIndex = i;
        }

        return newIndex > oldIndex;
    }
    private void updateTabSelection(TextView selectedTab) {
        // Reset all tabs
        resetTab(tvAll);
        resetTab(tvBreakfast);
        resetTab(tvLunch);
        resetTab(tvDinner);

        // Highlight selected tab
        selectedTab.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedTab.setTextColor(Color.WHITE);
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_chip_unselected);
        tab.setTextColor(Color.parseColor("#0F955A"));
    }
}