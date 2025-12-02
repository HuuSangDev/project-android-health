// File: app/src/main/java/com/example/app_selfcare/SavedRecipesActivity.java
package com.example.app_selfcare;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.SavedRecipeAdapter;
import com.example.app_selfcare.Data.Model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedRecipeAdapter adapter;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Món đã lưu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_saved_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        adapter = new SavedRecipeAdapter(recipeList, this);
        recyclerView.setAdapter(adapter);

        // Tải dữ liệu đã lưu
        loadSavedRecipes();

        // Nút Home dưới cùng (nếu có)
        findViewById(R.id.homeIcon).setOnClickListener(v -> finish());
    }

    private void loadSavedRecipes() {
        SharedPreferences prefs = getSharedPreferences("SavedRecipes", MODE_PRIVATE);
        String savedData = prefs.getString("recipes", "");

        if (savedData == null || savedData.isEmpty()) {
            // Nếu chưa có dữ liệu → thêm mẫu
            saveSampleData();
            savedData = prefs.getString("recipes", "");
        }

        recipeList.clear();

        if (!savedData.isEmpty()) {
            String[] items = savedData.split(";");
            for (String item : items) {
                if (!item.trim().isEmpty()) {
                    String[] parts = item.split("\\|");
                    if (parts.length >= 2) {
                        String name = parts[0].trim();
                        String timeInfo = parts[1].trim();

                        // Tạo Recipe dùng đúng model chung
                        Recipe recipe = new Recipe(
                                0, // id tạm (có thể bỏ qua nếu không dùng)
                                name,
                                "Món ngon được lưu từ ứng dụng", // description
                                R.drawable.ic_platter_background, // ảnh mẫu
                                extractMinutes(timeInfo),         // thời gian
                                250, // calo tạm
                                "Trung bình",
                                "DINNER"
                        );
                        recipeList.add(recipe);
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    // Trích xuất số phút từ chuỗi như "20 min"
    private int extractMinutes(String timeText) {
        try {
            return Integer.parseInt(timeText.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return 30; // mặc định
        }
    }

    private void saveSampleData() {
        SharedPreferences prefs = getSharedPreferences("SavedRecipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Chỉ lưu 1 lần
        if (prefs.getString("recipes", "").isEmpty()) {
            String sample =
                    "Sườn nướng truyền thống|20 phút;" +
                            "Cơm gà nướng mật ong|35 phút;" +
                            "Salad rau củ quả tươi|15 phút;" +
                            "Bánh mì kẹp thịt nướng|25 phút;";

            editor.putString("recipes", sample);
            editor.apply();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}