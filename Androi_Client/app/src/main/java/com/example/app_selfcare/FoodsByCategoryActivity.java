package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.FoodAdapter;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodsByCategoryActivity extends AppCompatActivity {

    private static final String TAG = "FoodsByCategoryActivity";

    private RecyclerView recyclerView;
    private View layoutLoading;
    private View layoutEmpty;
    private Toolbar toolbar;

    private FoodAdapter adapter;
    private ApiService apiService;
    private List<Food> foodList = new ArrayList<>();

    private long categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_by_category);

        // Lấy dữ liệu từ Intent
        categoryId = getIntent().getLongExtra("categoryId", -1);
        categoryName = getIntent().getStringExtra("categoryName");

        if (categoryId == -1) {
            Toast.makeText(this, "Không tìm thấy danh mục", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadFoods();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewFoods);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryName != null ? categoryName : "Danh mục");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new FoodAdapter(foodList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadFoods() {
        showLoading(true);

        apiService.getFoodsByCategory(categoryId).enqueue(new Callback<ApiResponse<List<FoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodResponse>>> call,
                                   Response<ApiResponse<List<FoodResponse>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().getResult() != null) {
                    foodList.clear();
                    for (FoodResponse foodResponse : response.body().getResult()) {
                        foodList.add(foodResponse.toFood());
                    }
                    adapter.notifyDataSetChanged();

                    showEmpty(foodList.isEmpty());
                } else {
                    Log.e(TAG, "Load foods failed: " + response.code());
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodResponse>>> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                Log.e(TAG, "Load foods error", t);
                Toast.makeText(FoodsByCategoryActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
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
}
