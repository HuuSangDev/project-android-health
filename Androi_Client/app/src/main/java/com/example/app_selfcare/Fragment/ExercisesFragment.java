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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.AddExerciseActivity;
import com.example.app_selfcare.Adapter.ExerciseAdapter;
import com.example.app_selfcare.Data.Model.Exercise;
import com.example.app_selfcare.Data.local.ExerciseStorage;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private ExerciseAdapter adapter;
    private final List<Exercise> exerciseList = new ArrayList<>();

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
        adapter = new ExerciseAdapter(exerciseList, new ExerciseAdapter.OnExerciseClickListener() {
            @Override
            public void onEdit(Exercise exercise) {
                Toast.makeText(requireContext(), "Nhấn giữ để sửa sau", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(int id) {
                ExerciseStorage.deleteExercise(requireContext(), id);
                loadExercises();
            }
        });
        recyclerView.setAdapter(adapter);
        loadExercises();
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExerciseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExercises();
    }

    private void loadExercises() {
        exerciseList.clear();
        exerciseList.addAll(ExerciseStorage.getExercises(requireContext()));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}