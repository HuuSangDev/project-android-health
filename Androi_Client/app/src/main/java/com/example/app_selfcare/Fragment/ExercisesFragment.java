package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExercisesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewExercises);
        fabAdd = view.findViewById(R.id.fabAddExercise);

        setupRecyclerView();
        setupFabButton();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Set adapter with exercise list
        // recyclerView.setAdapter(new ExerciseAdapter(exerciseList));
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            // TODO: Open dialog or activity to add new exercise
            // Show add exercise dialog or navigate to add exercise screen
        });
    }
}