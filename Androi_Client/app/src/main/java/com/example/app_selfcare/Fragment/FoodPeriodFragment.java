// File: app/src/main/java/com/example/app_selfcare/Fragment/FoodPeriodFragment.java
package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.FoodPeriodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Ingredient;
import com.example.app_selfcare.Data.Model.Step;
import com.example.app_selfcare.R;

import java.util.ArrayList;
import java.util.List;

public class FoodPeriodFragment extends Fragment {

    private static final String ARG_MEAL_TYPE = "mealType";
    private String mealType;

    private RecyclerView recyclerView;
    private FoodPeriodAdapter adapter;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    public static FoodPeriodFragment newInstance(String mealType) {
        FoodPeriodFragment fragment = new FoodPeriodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_TYPE, mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealType = getArguments().getString(ARG_MEAL_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_period, container, false);

        initViews(view);
        setupRecyclerView();
        loadSampleFoods(); // Dùng dữ liệu mẫu ngay trong fragment

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        if (mealType != null) {
            switch (mealType) {
                case "BREAKFAST":
                    tvEmptyMessage.setText("Chưa có món ăn sáng nào");
                    break;
                case "LUNCH":
                    tvEmptyMessage.setText("Chưa có món ăn trưa nào");
                    break;
                case "DINNER":
                    tvEmptyMessage.setText("Chưa có món ăn tối nào");
                    break;
                case "ALL":
                    tvEmptyMessage.setText("Chưa có món ăn nào");
                    break;
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new FoodPeriodAdapter(requireContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    // DỮ LIỆU MẪU – BẠN CÓ THỂ XÓA HOẶC THAY SAU NÀY
    private void loadSampleFoods() {
        List<Food> allFoods = new ArrayList<>();

        // Nguyên liệu mẫu
        List<Ingredient> ing1 = new ArrayList<>();
        ing1.add(new Ingredient("Tomato", "Cà chua", "2 quả"));
        ing1.add(new Ingredient("Leaf", "Rau xà lách", "100g"));

        List<Ingredient> ing2 = new ArrayList<>();
        ing2.add(new Ingredient("Rice", "Gạo lứt", "200g"));
        ing2.add(new Ingredient("Fish", "Cá hồi", "150g"));

        // Các bước mẫu
        List<Step> steps1 = new ArrayList<>();
        steps1.add(new Step("1", "Rửa sạch rau củ"));
        steps1.add(new Step("2", "Trộn với sốt dầu giấm"));

        List<Step> steps2 = new ArrayList<>();
        steps2.add(new Step("1", "Nấu cơm"));
        steps2.add(new Step("2", "Áp chảo cá hồi"));

        // Thêm món mẫu
        allFoods.add(new Food("Salad rau củ", "Tươi mát, ít calo", 150, 10, "Dễ", "BREAKFAST", ing1, steps1));
        allFoods.add(new Food("Yến mạch trái cây", "Bữa sáng lành mạnh", 220, 5, "Dễ", "BREAKFAST", ing1, steps1));
        allFoods.add(new Food("Cơm gạo lứt cá hồi", "Giàu protein", 480, 30, "Trung bình", "LUNCH", ing2, steps2));
        allFoods.add(new Food("Gà luộc rau củ", "Eat clean chuẩn", 350, 40, "Dễ", "LUNCH", ing2, steps2));
        allFoods.add(new Food("Súp bí đỏ", "Ấm bụng buổi tối", 120, 20, "Dễ", "DINNER", ing1, steps1));
        allFoods.add(new Food("Cá hấp gừng", "Thanh đạm, tốt cho sức khỏe", 280, 25, "Trung bình", "DINNER", ing2, steps2));

        // Lọc theo mealType
        List<Food> filteredFoods = new ArrayList<>();
        if (mealType == null || "ALL".equals(mealType)) {
            filteredFoods = allFoods;
        } else {
            for (Food food : allFoods) {
                if (mealType.equals(food.getMealType())) {
                    filteredFoods.add(food);
                }
            }
        }

        if (filteredFoods.isEmpty()) {
            showEmpty();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter.setFoodList(filteredFoods);
        }
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}