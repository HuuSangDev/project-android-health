// File: app/src/main/java/com/example/app_selfcare/SavedFoodActivity.java
package com.example.app_selfcare;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.SavedFoodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Ingredient;
import com.example.app_selfcare.Data.Model.Step;

import java.util.ArrayList;
import java.util.List;

public class SavedFoodActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedFoodAdapter adapter;
    private List<Food> savedFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes); // có thể đổi tên layout sau

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Món ăn đã lưu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_saved_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        savedFoodList = new ArrayList<>();
        adapter = new SavedFoodAdapter(savedFoodList, this);
        recyclerView.setAdapter(adapter);

        loadSavedFoods();

        findViewById(R.id.homeIcon).setOnClickListener(v -> finish());
    }

    private void loadSavedFoods() {
        SharedPreferences prefs = getSharedPreferences("SavedFoods", MODE_PRIVATE);
        String data = prefs.getString("saved_foods", "");

        savedFoodList.clear();

        if (data == null || data.isEmpty()) {
            createSampleData();
            data = prefs.getString("saved_foods", "");
        }

        if (!data.isEmpty()) {
            String[] items = data.split(";");
            for (String item : items) {
                if (item.trim().isEmpty()) continue;

                String[] parts = item.split("\\|");
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                int minutes = extractMinutes(parts[1].trim());

                // Tạo món ăn mẫu đầy đủ
                List<Ingredient> ingredients = new ArrayList<>();
                ingredients.add(new Ingredient("Leaf", "Rau củ các loại", "phù hợp"));

                List<Step> steps = new ArrayList<>();
                steps.add(new Step("1", "Chuẩn bị nguyên liệu"));
                steps.add(new Step("2", "Nấu theo hướng dẫn"));

                Food food = new Food(
                        name,
                        "Món bạn đã lưu từ gợi ý",
                        280,
                        minutes,
                        "Trung bình",
                        "LUNCH",
                        ingredients,
                        steps
                );
                food.setId("saved_" + name.hashCode());

                savedFoodList.add(food);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private int extractMinutes(String text) {
        try {
            String num = text.replaceAll("\\D+", "");
            return num.isEmpty() ? 20 : Integer.parseInt(num);
        } catch (Exception e) {
            return 20;
        }
    }

    private void createSampleData() {
        SharedPreferences prefs = getSharedPreferences("SavedFoods", MODE_PRIVATE);
        if (!prefs.getString("saved_foods", "").isEmpty()) return;

        String sample = "Cá hồi áp chảo|20 phút;" +
                "Gà nướng mật ong|35 phút;" +
                "Salad Hy Lạp|15 phút;" +
                "Bánh mì trứng ốp la|10 phút;" +
                "Súp bí đỏ kem tươi|25 phút;";

        prefs.edit().putString("saved_foods", sample).apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}