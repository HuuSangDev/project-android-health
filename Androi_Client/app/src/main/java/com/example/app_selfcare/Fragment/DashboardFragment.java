package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.app_selfcare.R;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup dashboard cards with statistics
        setupDashboardCards(view);
    }

    private void setupDashboardCards(View view) {
        CardView cardTotalUsers = view.findViewById(R.id.cardTotalUsers);
        CardView cardTotalExercises = view.findViewById(R.id.cardTotalExercises);
        CardView cardTotalFoods = view.findViewById(R.id.cardTotalFoods);
        CardView cardActiveUsers = view.findViewById(R.id.cardActiveUsers);

        // Add click listeners if needed
        cardTotalUsers.setOnClickListener(v -> {
            // Navigate to users management
        });

        cardTotalExercises.setOnClickListener(v -> {
            // Navigate to exercises management
        });

        cardTotalFoods.setOnClickListener(v -> {
            // Navigate to foods management
        });

        cardActiveUsers.setOnClickListener(v -> {
            // Show active users details
        });
    }
}