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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.AddFoodCategoryDialog;
import com.example.app_selfcare.Adapter.FoodCategoryAdapter;
import com.example.app_selfcare.Data.Model.Request.FoodCategoryCreateRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCategoryResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.FoodsByCategoryActivity;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodCategoriesFragment extends Fragment implements FoodCategoryAdapter.OnCategoryActionListener {

    private static final String TAG = "FoodCategoriesFragment";

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private View layoutLoading;
    private View layoutEmpty;
    private TextView tvTotalCategories;
    private TextView tvActiveCategories;

    private FoodCategoryAdapter adapter;
    private ApiService apiService;
    private List<FoodCategoryResponse> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerView();
        setupFabButton();
        loadCategories();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewFoodCategories);
        fabAdd = view.findViewById(R.id.fabAddCategory);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvTotalCategories = view.findViewById(R.id.tvTotalCategories);
        tvActiveCategories = view.findViewById(R.id.tvActiveCategories);
    }

    private void setupRecyclerView() {
        adapter = new FoodCategoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        showLoading(true);

        apiService.getAllFoodCategories().enqueue(new Callback<ApiResponse<List<FoodCategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodCategoryResponse>>> call,
                                   Response<ApiResponse<List<FoodCategoryResponse>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().getResult() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getResult());
                    adapter.setData(categoryList);

                    // Cập nhật thống kê
                    updateStats();

                    showEmpty(categoryList.isEmpty());
                } else {
                    Log.e(TAG, "Load categories failed: " + response.code());
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodCategoryResponse>>> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                Log.e(TAG, "Load categories error", t);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateStats() {
        int total = categoryList.size();
        int active = 0;
        for (FoodCategoryResponse cat : categoryList) {
            if (cat.getFoodCount() > 0) {
                active++;
            }
        }
        tvTotalCategories.setText(String.valueOf(total));
        tvActiveCategories.setText(String.valueOf(active));
    }

    private void showAddCategoryDialog() {
        AddFoodCategoryDialog dialog = new AddFoodCategoryDialog();
        dialog.setListener(category -> {
            Toast.makeText(getContext(), "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
            loadCategories();
        });
        dialog.show(getParentFragmentManager(), "AddFoodCategoryDialog");
    }

    // ==================== OnCategoryActionListener ====================

    @Override
    public void onItemClick(FoodCategoryResponse category) {
        // Mở màn hình danh sách món ăn theo danh mục
        Intent intent = new Intent(requireContext(), FoodsByCategoryActivity.class);
        intent.putExtra("categoryId", category.getCategoryId());
        intent.putExtra("categoryName", category.getCategoryName());
        startActivity(intent);
    }

    @Override
    public void onEditClick(FoodCategoryResponse category) {
        showEditCategoryDialog(category);
    }

    @Override
    public void onDeleteClick(FoodCategoryResponse category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục \"" + category.getCategoryName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCategory(category))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditCategoryDialog(FoodCategoryResponse category) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_food_category, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etCategoryName);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etCategoryDescription);

        etName.setText(category.getCategoryName());
        etDescription.setText(category.getDescription());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Xử lý sự kiện cho các nút trong layout
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnSaveCategory).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

            if (name.isEmpty()) {
                etName.setError("Nhập tên danh mục");
                return;
            }

            updateCategory(category.getCategoryId(), name, desc);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateCategory(long categoryId, String name, String description) {
        showLoading(true);

        FoodCategoryCreateRequest request = new FoodCategoryCreateRequest(name, description);

        apiService.updateFoodCategory(categoryId, request).enqueue(new Callback<ApiResponse<FoodCategoryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodCategoryResponse>> call,
                                   Response<ApiResponse<FoodCategoryResponse>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã cập nhật danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodCategoryResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(FoodCategoryResponse category) {
        showLoading(true);

        apiService.deleteFoodCategory(category.getCategoryId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmpty(boolean show) {
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }
}
