package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.R;

public class IngredientsFragment extends Fragment {

    private FoodResponse food;

    public static IngredientsFragment newInstance(FoodResponse food) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putSerializable("food_data", food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = (FoodResponse) getArguments().getSerializable("food_data");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        if (food != null) {
            TextView tvCalories = view.findViewById(R.id.tv_calories_val);
            TextView tvProtein = view.findViewById(R.id.tv_protein_val);
            TextView tvFat = view.findViewById(R.id.tv_fat_val);
            TextView tvFiber = view.findViewById(R.id.tv_fiber_val);
            TextView tvSugar = view.findViewById(R.id.tv_sugar_val);
            TextView tvServings = view.findViewById(R.id.tv_servings_val);

            tvCalories.setText(food.getCaloriesPer100g() + " kcal");
            tvProtein.setText(food.getProteinPer100g() + "g");
            tvFat.setText(food.getFatPer100g() + "g");
            tvFiber.setText(food.getFiberPer100g() + "g");
            tvSugar.setText(food.getSugarPer100g() + "g");
            tvServings.setText(String.valueOf(food.getServings()));
        }

        return view;
    }
}
