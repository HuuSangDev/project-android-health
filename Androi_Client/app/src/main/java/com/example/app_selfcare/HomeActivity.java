package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_selfcare.Fragment.SearchFragment;

public class HomeActivity extends AppCompatActivity {
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        // ========== SEARCH ==========
        findViewById(R.id.layoutSearch).setOnClickListener(v -> {
            openSearchFragment();
        });


        // See All Diet → RecipeHome
        findViewById(R.id.tvSeeAllDiet).setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Card Recipe
        findViewById(R.id.cardRecipeSample).setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Card Workout
        findViewById(R.id.cardWorkoutSample).setOnClickListener(v -> {
            startActivity(new Intent(this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Bottom Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navWorkout).setOnClickListener(v -> {
            startActivity(new Intent(this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        findViewById(R.id.navPlanner).setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Chat FAB
        findViewById(R.id.fabChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    // ================= SEARCH FRAGMENT =================
    private void openSearchFragment() {
        SearchFragment fragment = new SearchFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // SỬA THÀNH ĐOẠN NÀY ĐỂ DÙNG FADE NHANH
        transaction.setCustomAnimations(
                R.anim.fade_in_fast,
                0,
                0,
                R.anim.fade_out_fast
        );

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack("SearchFragment");
        transaction.commit();

        // Hiện container ngay lập tức
        fragmentContainer.setVisibility(View.VISIBLE);
    }

    // ================= BACK HANDLING =================
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            // Ẩn container khi thoát fragment (tùy chọn, giúp tối ưu)
            fragmentContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
