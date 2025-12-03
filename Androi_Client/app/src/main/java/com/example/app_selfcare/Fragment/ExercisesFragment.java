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

public class ExercisesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private List<String> exerciseList;

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

        // Sample data
        exerciseList = new ArrayList<>();
        exerciseList.add("Push-ups - 20 reps");
        exerciseList.add("Squats - 30 reps");
        exerciseList.add("Plank - 60 seconds");
        exerciseList.add("Running - 5km");
        exerciseList.add("Jumping Jacks - 50 reps");

        setupRecyclerView();
        setupFabButton();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Create and set actual adapter
        // ExerciseAdapter adapter = new ExerciseAdapter(exerciseList);
        // recyclerView.setAdapter(adapter);
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thêm bài tập mới", Toast.LENGTH_SHORT).show();
            // TODO: Open add exercise dialog or activity
            // Intent intent = new Intent(getActivity(), AddExerciseActivity.class);
            // startActivity(intent);
        });
    }
}