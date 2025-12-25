package com.example.app_selfcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_selfcare.AddExerciseActivity;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.StatisticsResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";

    private TextView tvTotalUsers, tvTotalExercises, tvTotalFoods;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupApiService();
        setupDashboardCards(view);
        loadStatistics();
    }

    private void initViews(View view) {
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalExercises = view.findViewById(R.id.tvTotalExercises);
        tvTotalFoods = view.findViewById(R.id.tvTotalFoods);
    }

    private void setupApiService() {
        apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);
    }

    private void setupDashboardCards(View view) {
        CardView cardTotalExercises = view.findViewById(R.id.cardTotalExercises);
        CardView cardTotalFoods = view.findViewById(R.id.cardTotalFoods);
        CardView btnQuickAddExercise = view.findViewById(R.id.btnQuickAddExercise);
        CardView btnQuickAddFood = view.findViewById(R.id.btnQuickAddFood);
        CardView btnQuickNotification = view.findViewById(R.id.btnQuickNotification);

        cardTotalExercises.setOnClickListener(v -> navigateToFragment(new ExercisesFragment(), "Quản lí Bài tập"));
        btnQuickAddExercise.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExerciseActivity.class);
            startActivity(intent);
        });
        cardTotalFoods.setOnClickListener(v -> navigateToFragment(new FoodsFragment(), "Quản lí Món ăn"));
        btnQuickAddFood.setOnClickListener(v -> navigateToFragment(new FoodsFragment(), "Quản lí Món ăn"));
        btnQuickNotification.setOnClickListener(v -> navigateToFragment(new NotificationsFragment(), "Thông báo"));
    }

    private void loadStatistics() {
        apiService.getOverviewStatistics().enqueue(new Callback<ApiResponse<StatisticsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<StatisticsResponse>> call, Response<ApiResponse<StatisticsResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    StatisticsResponse stats = response.body().getResult();
                    updateDashboardNumbers(stats);
                    Log.d(TAG, "Dashboard statistics loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load dashboard statistics: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<StatisticsResponse>> call, Throwable t) {
                Log.e(TAG, "Error loading dashboard statistics", t);
            }
        });
    }

    private void updateDashboardNumbers(StatisticsResponse stats) {
        if (tvTotalUsers != null) {
            tvTotalUsers.setText(String.valueOf(stats.getTotalUsers()));
        }
        if (tvTotalExercises != null) {
            tvTotalExercises.setText(String.valueOf(stats.getTotalExercises()));
        }
        if (tvTotalFoods != null) {
            tvTotalFoods.setText(String.valueOf(stats.getTotalFoods()));
        }
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

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }
}