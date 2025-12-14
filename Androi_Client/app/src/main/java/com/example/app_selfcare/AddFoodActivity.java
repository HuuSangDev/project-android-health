// File: app/src/main/java/com/example/app_selfcare/AddFoodActivity.java
package com.example.app_selfcare;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.IngredientsAdapter;
import com.example.app_selfcare.Adapter.StepsAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Ingredient;
import com.example.app_selfcare.Data.Model.Step;
import com.example.app_selfcare.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddFoodActivity extends AppCompatActivity {

    private TextInputEditText etFoodName, etDescription, etCalories, etTime;
    private MaterialAutoCompleteTextView actvDifficulty;
    private RecyclerView rvIngredients, rvSteps;
    private TextInputEditText etAddIngredient, etAddStep;

    private List<Ingredient> ingredientList = new ArrayList<>();
    private List<Step> stepList = new ArrayList<>();
    private IngredientsAdapter ingredientAdapter;
    private StepsAdapter stepAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        initViews();
        setupDropdown();
        setupRecyclerViews();
        setupClickListeners();
    }

    private void initViews() {
        etFoodName = findViewById(R.id.etFoodName);
        etDescription = findViewById(R.id.etDescription);
        etCalories = findViewById(R.id.etCalories);
        etTime = findViewById(R.id.etTime);
        actvDifficulty = findViewById(R.id.actvDifficulty);

        rvIngredients = findViewById(R.id.rvIngredients);
        rvSteps = findViewById(R.id.rvSteps);

        etAddIngredient = findViewById(R.id.etAddIngredient);
        etAddStep = findViewById(R.id.etAddStep);
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.exercise_difficulties)
        );
        actvDifficulty.setAdapter(adapter);
        actvDifficulty.setOnClickListener(v -> actvDifficulty.showDropDown());
    }

    private void setupRecyclerViews() {
        ingredientAdapter = new IngredientsAdapter(ingredientList);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvIngredients.setAdapter(ingredientAdapter);

        stepAdapter = new StepsAdapter(stepList);
        rvSteps.setLayoutManager(new LinearLayoutManager(this));
        rvSteps.setAdapter(stepAdapter);
    }

    private void setupClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> {
            String text = etAddIngredient.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                ingredientList.add(new Ingredient("Tomato", text, "1 phần"));
                ingredientAdapter.notifyItemInserted(ingredientList.size() - 1);
                etAddIngredient.setText("");
            }
        });

        findViewById(R.id.btnAddStep).setOnClickListener(v -> {
            String desc = etAddStep.getText().toString().trim();
            if (!TextUtils.isEmpty(desc)) {
                int stepNum = stepList.size() + 1;
                stepList.add(new Step(String.valueOf(stepNum), desc));
                stepAdapter.notifyItemInserted(stepList.size() - 1);
                etAddStep.setText("");
            }
        });

        findViewById(R.id.btnSaveFood).setOnClickListener(v -> saveFood());
    }

    private void saveFood() {
        String name = etFoodName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String calStr = etCalories.getText().toString();
        String timeStr = etTime.getText().toString();
        String difficulty = actvDifficulty.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nhập tên món ăn", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ingredientList.isEmpty() || stepList.isEmpty()) {
            Toast.makeText(this, "Cần ít nhất 1 nguyên liệu và 1 bước nấu", Toast.LENGTH_SHORT).show();
            return;
        }

        int calories = TextUtils.isEmpty(calStr) ? 0 : Integer.parseInt(calStr);
        int time = TextUtils.isEmpty(timeStr) ? 30 : Integer.parseInt(timeStr);
        if (TextUtils.isEmpty(difficulty)) difficulty = "Trung bình";

        Food newFood = new Food(
                name, desc, calories, time, difficulty, "LUNCH",
                new ArrayList<>(ingredientList), new ArrayList<>(stepList)
        );

        // Lưu tạm vào SharedPreferences để hiển thị
        com.example.app_selfcare.Data.local.FoodStorage.addFood(this, newFood);

        Toast.makeText(this, "Thêm món \"" + name + "\" thành công!", Toast.LENGTH_LONG).show();
        finish();
    }
}