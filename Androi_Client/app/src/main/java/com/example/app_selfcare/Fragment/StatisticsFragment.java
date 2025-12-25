package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.StatisticsResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsFragment extends Fragment {

    private static final String TAG = "StatisticsFragment";

    private TextView tvTotalUsers, tvActiveUsers, tvTotalExercises, tvTotalFoods, tvTotalNotifications;
    private View layoutLoading;

    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupApiService();
        loadStatistics();
    }

    private void initViews(View view) {
        // TextViews để hiển thị số liệu
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvActiveUsers = view.findViewById(R.id.tvActiveUsers);
        tvTotalExercises = view.findViewById(R.id.tvTotalExercises);
        tvTotalFoods = view.findViewById(R.id.tvTotalFoods);
        tvTotalNotifications = view.findViewById(R.id.tvTotalNotifications);

        layoutLoading = view.findViewById(R.id.layoutLoading);
    }

    private void setupApiService() {
        apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);
    }

    private void loadStatistics() {
        showLoading(true);

        apiService.getOverviewStatistics().enqueue(new Callback<ApiResponse<StatisticsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<StatisticsResponse>> call, Response<ApiResponse<StatisticsResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    StatisticsResponse stats = response.body().getResult();
                    displayStatistics(stats);
                    Log.d(TAG, "Statistics loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load statistics: " + response.code());
                    showError("Không thể tải thống kê");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<StatisticsResponse>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Error loading statistics", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void displayStatistics(StatisticsResponse stats) {
        if (tvTotalUsers != null) {
            tvTotalUsers.setText(String.valueOf(stats.getTotalUsers()));
        }
        if (tvActiveUsers != null) {
            tvActiveUsers.setText(String.valueOf(stats.getActiveUsers()));
        }
        if (tvTotalExercises != null) {
            tvTotalExercises.setText(String.valueOf(stats.getTotalExercises()));
        }
        if (tvTotalFoods != null) {
            tvTotalFoods.setText(String.valueOf(stats.getTotalFoods()));
        }
        if (tvTotalNotifications != null) {
            tvTotalNotifications.setText(String.valueOf(stats.getTotalNotifications()));
        }

        Log.d(TAG, "Statistics displayed: Users=" + stats.getTotalUsers() + 
                ", Exercises=" + stats.getTotalExercises() + 
                ", Foods=" + stats.getTotalFoods());
    }

    private void showLoading(boolean show) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh statistics when fragment becomes visible
        loadStatistics();
    }
}