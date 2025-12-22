package com.example.app_selfcare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.app_selfcare.Fragment.FoodPeriodFragment;

public class RecipeHomeActivity extends BaseActivity {

    private View navHome, navWorkout, navPlanner, navProfile;
    private TextView tvAll, tvBreakfast, tvLunch, tvDinner, tvSaved;

    private String currentMealType = "ALL";

    private Fragment allFragment, breakfastFragment, lunchFragment, dinnerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_home);

        initViews();
        setupClickListeners();

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
}
