package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FoodsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

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

    private void setupRecyclerView() {
        // Grid layout with 2 columns for food items
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // TODO: Set adapter with food list
        // recyclerView.setAdapter(new FoodAdapter(foodList));
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            // TODO: Open dialog or activity to add new food
        });
    }
}