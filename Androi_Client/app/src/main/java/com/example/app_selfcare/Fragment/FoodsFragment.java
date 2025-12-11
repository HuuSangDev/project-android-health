package com.example.app_selfcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.FoodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Ingredient;
import com.example.app_selfcare.Data.Model.Step;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class FoodsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private List<String> foodList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_foods, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFoods);
        fabAdd = view.findViewById(R.id.fabAddFood);

        // Sample data
        foodList = new ArrayList<>();
        foodList.add("Salad rau củ - 150 kcal");
        foodList.add("Cơm gạo lứt - 200 kcal");
        foodList.add("Ức gà nướng - 250 kcal");
        foodList.add("Cá hồi nướng - 300 kcal");
        foodList.add("Sinh tố bơ - 180 kcal");
        foodList.add("Súp bí đỏ - 120 kcal");

        setupRecyclerView();
        setupFabButton();
    }

    // Chỉ sửa phần setupRecyclerView()
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<Food> foodList = new ArrayList<>();
        // Thêm dữ liệu mẫu
        List<Ingredient> ing = new ArrayList<>();
        ing.add(new Ingredient("Tomato", "Cà chua", "2 quả"));
        List<Step> steps = new ArrayList<>();
        steps.add(new Step("1", "Rửa sạch cà chua..."));

        foodList.add(new Food("Salad rau củ", "Healthy và ngon", 150, 10, "Dễ", "LUNCH", ing, steps));
        // thêm món khác...

        FoodAdapter adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thêm món ăn mới", Toast.LENGTH_SHORT).show();
            // TODO: Open add food dialog or activity
//             Intent intent = new Intent(getActivity(), AddFoodActivity.class);
//             startActivity(intent);
        });
    }
}