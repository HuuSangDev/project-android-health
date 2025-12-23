package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.SavedFoodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.utils.LocaleManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedFoodActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedFoodAdapter adapter;
    private final List<Food> foodList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes); // XML bạn gửi

        // ================= RecyclerView =================
        recyclerView = findViewById(R.id.recyclerSavedFood);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SavedFoodAdapter(foodList, this);
        recyclerView.setAdapter(adapter);

        loadSavedFoods();

        // ================= HEADER =================
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // ================= BOTTOM NAV =================
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
    }

    private void loadSavedFoods() {
        SharedPreferences prefs = getSharedPreferences("SAVED_FOODS", MODE_PRIVATE);
        String json = prefs.getString("foods", "");

        foodList.clear();

        if (json != null && !json.isEmpty()) {
            Type type = new TypeToken<List<Food>>() {}.getType();
            List<Food> savedFoods = new Gson().fromJson(json, type);
            if (savedFoods != null) {
                foodList.addAll(savedFoods);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
