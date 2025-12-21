package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentTransaction;

import com.example.app_selfcare.Fragment.SearchFragment;

public class HomeActivity extends BaseActivity {

    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ⚠️ BẮT BUỘC gọi setContentView
        setContentView(R.layout.activity_home);

        fragmentContainer = findViewById(R.id.fragmentContainer);

        setupClickListeners();

        // ✅ HIỆN LOADING CHUNG
        showLoading();

        // ❗ TẠM THỜI GIẢ LẬP LOAD (CHƯA CÓ API)
        fakeLoading();
    }

    // ================== GIẢ LẬP LOAD ==================
    private void fakeLoading() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            hideLoading();
        }, 1200); // 1.2s – nhìn rất tự nhiên
    }

    // ================== CLICK ==================
    private void setupClickListeners() {

        // SEARCH
        findViewById(R.id.layoutSearch).setOnClickListener(v -> openSearchFragment());

        // SEE ALL DIET
        findViewById(R.id.tvSeeAllDiet).setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // CARD RECIPE
        findViewById(R.id.cardRecipeSample).setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // CARD WORKOUT
        findViewById(R.id.cardWorkoutSample).setOnClickListener(v -> {
            startActivity(new Intent(this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // BOTTOM NAV
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

        findViewById(R.id.fabChat).setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));
    }

    // ================== SEARCH FRAGMENT ==================
    private void openSearchFragment() {

        SearchFragment fragment = new SearchFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(
                R.anim.fade_in_fast,
                0,
                0,
                R.anim.fade_out_fast
        );

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack("SearchFragment");
        transaction.commit();

        fragmentContainer.setVisibility(View.VISIBLE);
    }

    // ================== BACK ==================
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
