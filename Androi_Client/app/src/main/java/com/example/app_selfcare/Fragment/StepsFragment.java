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

public class StepsFragment extends Fragment {

    private FoodResponse food;

    public static StepsFragment newInstance(FoodResponse food) {
        StepsFragment fragment = new StepsFragment();
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
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        if (food != null) {
            TextView tvPrepTime = view.findViewById(R.id.tv_prep_time_val);
            TextView tvCookTime = view.findViewById(R.id.tv_cook_time_val);
            TextView tvDifficulty = view.findViewById(R.id.tv_difficulty_val);
            TextView tvInstructions = view.findViewById(R.id.tv_instructions_val);

            tvPrepTime.setText(food.getPrepTime() + " phút");
            tvCookTime.setText(food.getCookTime() + " phút");
            tvDifficulty.setText(convertDifficulty(food.getDifficultyLevel()));
            tvInstructions.setText(food.getInstructions());
        }

        return view;
    }

    private String convertDifficulty(String level) {
        if (level == null) return "Dễ";
        switch (level.toUpperCase()) {
            case "EASY": return "Dễ";
            case "MEDIUM": return "Trung bình";
            case "HARD": return "Khó";
            default: return level;
        }
    }
}
