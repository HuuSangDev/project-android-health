package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // See All Diet → RecipeHome
        findViewById(R.id.tvSeeAllDiet).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // CARD MÓN ĂN → RECIPEHOME (THÊM DÒNG NÀY)
        findViewById(R.id.cardRecipeSample).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // CARD BÀI TẬP (sẽ làm sau)
        findViewById(R.id.cardWorkoutSample).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // See All Workouts (sẽ làm sau)
        findViewById(R.id.tvSeeAllWorkouts).setOnClickListener(v -> {
            // Sẽ làm sau
        });

        // Bottom navigation
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navWorkout).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        findViewById(R.id.navPlanner).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Chat FAB button
        findViewById(R.id.fabChat).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ChatActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}