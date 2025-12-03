package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ExerciseCategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private List<String> categoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewExerciseCategories);
        fabAdd = view.findViewById(R.id.fabAddExerciseCategory);

        // Sample data
        categoryList = new ArrayList<>();
        categoryList.add("Cardio - 25 bài tập");
        categoryList.add("Strength - 30 bài tập");
        categoryList.add("Yoga - 20 bài tập");
        categoryList.add("HIIT - 15 bài tập");
        categoryList.add("Stretching - 18 bài tập");

        setupRecyclerView();
        setupFabButton();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Set adapter
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thêm danh mục bài tập mới", Toast.LENGTH_SHORT).show();
        });
    }
}