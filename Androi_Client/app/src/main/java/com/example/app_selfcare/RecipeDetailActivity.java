package com.example.app_selfcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_selfcare.Adapter.FoodPagerAdapter;
import com.example.app_selfcare.Fragment.IngredientsFragment;
import com.example.app_selfcare.Fragment.StepsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView backButton, ivRecipeImage, saveButton;
    private TextView tvRecipeTitle, tvRecipeTime;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    // Bottom nav (LinearLayout)
    private LinearLayout navHome, navWorkout, navPlanner, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        //=========================
        // 1. INIT VIEW
        //=========================
        backButton = findViewById(R.id.backButton);
        ivRecipeImage = findViewById(R.id.iv_recipe_image);
        tvRecipeTitle = findViewById(R.id.tv_recipe_title);
        tvRecipeTime = findViewById(R.id.tv_recipe_time);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        saveButton = findViewById(R.id.saveButton);

        // Bottom navigation layout IDs
        navHome = findViewById(R.id.navHome);
        navWorkout = findViewById(R.id.navWorkout);
        navPlanner = findViewById(R.id.navPlanner);
        navProfile = findViewById(R.id.navProfile);


        //=========================
        // 2. LẤY DỮ LIỆU TỪ INTENT
        //=========================
        String recipeName = getIntent().getStringExtra("recipeName");
        String recipeTime = getIntent().getStringExtra("recipeTime");

        if (recipeName != null) tvRecipeTitle.setText(recipeName);
        if (recipeTime != null) tvRecipeTime.setText("⏰ " + recipeTime);


        //=========================
        // 3. SETUP VIEWPAGER + TAB
        //=========================
        ArrayList<androidx.fragment.app.Fragment> fragments = new ArrayList<>();
        fragments.add(new IngredientsFragment());
        fragments.add(new StepsFragment());

        FoodPagerAdapter adapter = new FoodPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Thành phần");
            else tab.setText("Quy trình");
        }).attach();


        //=========================
        // 4. BACK BUTTON
        //=========================
        backButton.setOnClickListener(v -> onBackPressed());


        //=========================
        // 5. SAVE BUTTON
        //=========================
        saveButton.setOnClickListener(v -> {
            String recipeNameToSave = tvRecipeTitle.getText().toString();
            String recipeTimeToSave = tvRecipeTime.getText().toString().replace("⏰ ", "");
            saveRecipe(recipeNameToSave, recipeTimeToSave);
            Toast.makeText(this, "Đã lưu món: " + recipeNameToSave, Toast.LENGTH_SHORT).show();
        });


        //=========================
        // 6. BOTTOM NAVIGATION (LinearLayout)
        //=========================
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        navWorkout.setOnClickListener(v -> {
            startActivity(new Intent(this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        navPlanner.setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }


    //=========================
    // 7. HÀM LƯU MÓN ĂN
    //=========================
    private void saveRecipe(String name, String time) {
        SharedPreferences prefs = getSharedPreferences("SavedRecipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String savedRecipes = prefs.getString("recipes", "");

        if (!savedRecipes.contains(name)) {
            savedRecipes += name + "|" + time + ";";
            editor.putString("recipes", savedRecipes);
            editor.apply();
        }
    }
}
