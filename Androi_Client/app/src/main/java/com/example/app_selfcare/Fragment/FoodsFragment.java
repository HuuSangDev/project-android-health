package com.example.app_selfcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.AddFoodActivity;
import com.example.app_selfcare.Adapter.AdminFoodAdapter;
import com.example.app_selfcare.Data.Model.Request.FoodSearchRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCategoryResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.Model.Response.PageResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.FoodDetailActivity;
import com.example.app_selfcare.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class FoodsFragment extends Fragment implements AdminFoodAdapter.OnFoodClickListener {

    private static final String TAG = "FoodsFragment";

    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton fabAdd;
    private ChipGroup chipGroupCategories;
    private View layoutLoading;
    private View layoutEmptyState;

    private AdminFoodAdapter adapter;
    private ApiService apiService;

    private List<FoodCategoryResponse> categoryList = new ArrayList<>();
    private Long selectedCategoryId = null;

    private final ActivityResultLauncher<Intent> addFoodLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadFoods();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_foods, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerView();
        setupFabButton();

        loadCategories();
        loadFoods();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewFoods);
        fabAdd = view.findViewById(R.id.fabAddFood);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }

    private void setupRecyclerView() {
        adapter = new AdminFoodAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFoodActivity.class);
            addFoodLauncher.launch(intent);
        });
    }

    private void loadCategories() {
        apiService.getAllFoodCategories().enqueue(new Callback<ApiResponse<List<FoodCategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodCategoryResponse>>> call,
                                   Response<ApiResponse<List<FoodCategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getResult() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getResult());
                    setupCategoryChips();
                } else {
                    Log.e(TAG, "Load categories failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodCategoryResponse>>> call, Throwable t) {
                Log.e(TAG, "Load categories error", t);
            }
        });
    }

    private void setupCategoryChips() {
        if (chipGroupCategories == null) return;
        
        chipGroupCategories.removeAllViews();

        // Chip "Tất cả"
        Chip chipAll = new Chip(requireContext());
        chipAll.setText("Tất cả");
        chipAll.setCheckable(true);
        chipAll.setChecked(true);
        chipAll.setOnClickListener(v -> {
            selectedCategoryId = null;
            loadFoods();
        });
        chipGroupCategories.addView(chipAll);

        // Chips cho từng category
        for (FoodCategoryResponse category : categoryList) {
            Chip chip = new Chip(requireContext());
            chip.setText(category.getCategoryName());
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategoryId = category.getCategoryId();
                loadFoods();
            });
            chipGroupCategories.addView(chip);
        }
    }

    private void loadFoods() {
        showLoading(true);

        FoodSearchRequest request = FoodSearchRequest.builder()
                .categoryId(selectedCategoryId)
                .size(100)
                .build();

        apiService.searchFoods(request).enqueue(new Callback<ApiResponse<PageResponse<FoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<FoodResponse>>> call,
                                   Response<ApiResponse<PageResponse<FoodResponse>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().getResult() != null) {
                    List<FoodResponse> foods = response.body().getResult().getContent();
                    adapter.setData(foods);

                    if (foods == null || foods.isEmpty()) {
                        showEmpty(true);
                    } else {
                        showEmpty(false);
                    }
                } else {
                    Log.e(TAG, "Load foods failed: " + response.code());
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<FoodResponse>>> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                Log.e(TAG, "Load foods error", t);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải danh sách", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showLoading(boolean show) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmpty(boolean show) {
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // ==================== OnFoodClickListener ====================

    @Override
    public void onFoodClick(FoodResponse food) {
        Intent intent = new Intent(getActivity(), FoodDetailActivity.class);
        intent.putExtra("foodId", food.getFoodId());
        addFoodLauncher.launch(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFoods();
    }
}
