// File: app/src/main/java/com/example/app_selfcare/Fragment/FoodPeriodFragment.java
package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.FoodPeriodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodPeriodFragment extends Fragment {

    private static final String ARG_MEAL_TYPE = "mealType";
    private String mealType;

    private RecyclerView recyclerView;
    private FoodPeriodAdapter adapter;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;
    private ApiService apiService;

    public static FoodPeriodFragment newInstance(String mealType) {
        FoodPeriodFragment fragment = new FoodPeriodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_TYPE, mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealType = getArguments().getString(ARG_MEAL_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_period, container, false);

        initViews(view);
        setupRecyclerView();
        initApiService();
        loadFoodsFromApi(); // Gọi API thay vì dùng dữ liệu mẫu

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        if (mealType != null) {
            switch (mealType) {
                case "BREAKFAST":
                    tvEmptyMessage.setText("Chưa có món ăn sáng nào");
                    break;
                case "LUNCH":
                    tvEmptyMessage.setText("Chưa có món ăn trưa nào");
                    break;
                case "DINNER":
                    tvEmptyMessage.setText("Chưa có món ăn tối nào");
                    break;
                case "ALL":
                    tvEmptyMessage.setText("Chưa có món ăn nào");
                    break;
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new FoodPeriodAdapter(requireContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    private void initApiService() {
        // Sử dụng getClientWithToken để tự động thêm Bearer token vào header
        apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);
    }

    private void loadFoodsFromApi() {
        Call<com.example.app_selfcare.Data.Model.Response.ApiResponse<List<FoodResponse>>> call;

        if (mealType == null || "ALL".equals(mealType)) {
            // Gọi API lấy tất cả món ăn
            call = apiService.getAllFoods();
        } else {
            // Gọi API lấy món ăn theo mealType
            call = apiService.getFoodsByMealType(mealType);
        }

        call.enqueue(new Callback<com.example.app_selfcare.Data.Model.Response.ApiResponse<List<FoodResponse>>>() {
            @Override
            public void onResponse(Call<com.example.app_selfcare.Data.Model.Response.ApiResponse<List<FoodResponse>>> call,
                                   Response<com.example.app_selfcare.Data.Model.Response.ApiResponse<List<FoodResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.app_selfcare.Data.Model.Response.ApiResponse<List<FoodResponse>> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        List<FoodResponse> foodResponses = apiResponse.getResult();
                        List<Food> foods = convertToFoodList(foodResponses);

                        if (foods.isEmpty()) {
                            showEmpty();
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                            adapter.setFoodList(foods);
                        }
                    } else {
                        showEmpty();
                        Log.e("FoodPeriodFragment", "API Error: " + apiResponse.getMessage());
                    }
                } else {
                    showEmpty();
                    int statusCode = response.code();
                    if (statusCode == 401) {
                        Log.e("FoodPeriodFragment", "Unauthorized - Token missing or expired");
                        Toast.makeText(requireContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("FoodPeriodFragment", "Response error: " + response.message() + " (Code: " + statusCode + ")");
                        Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu: " + statusCode, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.app_selfcare.Data.Model.Response.ApiResponse<List<FoodResponse>>> call, Throwable t) {
                showEmpty();
                Log.e("FoodPeriodFragment", "API call failed", t);
                Toast.makeText(requireContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Food> convertToFoodList(List<FoodResponse> foodResponses) {
        List<Food> foods = new ArrayList<>();
        for (FoodResponse foodResponse : foodResponses) {
            Food food = foodResponse.toFood();
            foods.add(food);
        }
        return foods;
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}