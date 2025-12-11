package com.example.app_selfcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.AddFoodActivity;
import com.example.app_selfcare.Adapter.FoodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Ingredient;
import com.example.app_selfcare.Data.Model.Step;
import com.example.app_selfcare.Data.local.FoodStorage;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FoodsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private final List<Food> foodList = new ArrayList<>();
    private FoodAdapter adapter;

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

        setupRecyclerView();
        setupFabButton();
    }

    // Chỉ sửa phần setupRecyclerView()
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);
        loadFoods();
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFoodActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFoods();
    }

    private void loadFoods() {
        foodList.clear();
        foodList.addAll(FoodStorage.getFoods(requireContext()));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}