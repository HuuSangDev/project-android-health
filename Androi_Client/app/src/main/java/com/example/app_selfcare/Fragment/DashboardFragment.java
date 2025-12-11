package com.example.app_selfcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.app_selfcare.AddExerciseActivity;
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
        setupDashboardCards(view);
    }

    private void setupDashboardCards(View view) {
        CardView cardTotalUsers = view.findViewById(R.id.cardTotalUsers);
        CardView cardTotalExercises = view.findViewById(R.id.cardTotalExercises);
        CardView cardTotalFoods = view.findViewById(R.id.cardTotalFoods);
        CardView cardActiveUsers = view.findViewById(R.id.cardActiveUsers);
        CardView btnQuickAddExercise = view.findViewById(R.id.btnQuickAddExercise);
        CardView btnQuickAddFood = view.findViewById(R.id.btnQuickAddFood);
        CardView btnQuickNotification = view.findViewById(R.id.btnQuickNotification);

        cardTotalUsers.setOnClickListener(v -> navigateToFragment(new UsersFragment(), "Quản lí Người dùng"));
        cardActiveUsers.setOnClickListener(v -> navigateToFragment(new UsersFragment(), "Quản lí Người dùng"));
        cardTotalExercises.setOnClickListener(v -> navigateToFragment(new ExercisesFragment(), "Quản lí Bài tập"));
        btnQuickAddExercise.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExerciseActivity.class);
            startActivity(intent);
        });
        cardTotalFoods.setOnClickListener(v -> navigateToFragment(new FoodsFragment(), "Quản lí Món ăn"));
        btnQuickAddFood.setOnClickListener(v -> navigateToFragment(new FoodsFragment(), "Quản lí Món ăn"));
        btnQuickNotification.setOnClickListener(v -> navigateToFragment(new NotificationsFragment(), "Thông báo"));
    }

    private void navigateToFragment(Fragment fragment, String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}