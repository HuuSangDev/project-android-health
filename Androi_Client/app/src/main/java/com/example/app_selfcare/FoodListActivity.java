package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCreateResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.Adapter.FoodListAdapter;
import com.example.app_selfcare.utils.LocaleManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodListActivity extends AppCompatActivity {

    private static final String TAG = "FoodListActivity";
    
    private ImageView btnBack;
    private RecyclerView recyclerViewFoods;
    private ProgressBar progressBar;
    private FoodListAdapter adapter;
    private List<FoodCreateResponse> foodList;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initViews();
        setupRecyclerView();
        setupEvents();
        fetchAllFoods();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerViewFoods = findViewById(R.id.recyclerViewFoods);
        progressBar = findViewById(R.id.progressBar);
        
        foodList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new FoodListAdapter(this, foodList, this::onFoodItemClick);
        recyclerViewFoods.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFoods.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void onFoodItemClick(FoodCreateResponse food) {
        Intent intent = new Intent(this, FoodIngredientsActivity.class);
        intent.putExtra("FOOD_ID", food.getFoodId());
        startActivity(intent);
    }

    private void fetchAllFoods() {
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerViewFoods.setVisibility(View.GONE);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllFoods("Bearer " + token).enqueue(new Callback<ApiResponse<List<FoodCreateResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodCreateResponse>>> call, 
                                 Response<ApiResponse<List<FoodCreateResponse>>> response) {
                progressBar.setVisibility(View.GONE);
                recyclerViewFoods.setVisibility(View.VISIBLE);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(FoodListActivity.this, "Không thể tải danh sách thực phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<FoodCreateResponse> foods = response.body().getResult();
                if (foods != null && !foods.isEmpty()) {
                    foodList.clear();
                    foodList.addAll(foods);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + foods.size() + " foods");
                } else {
                    Toast.makeText(FoodListActivity.this, "Không có thực phẩm nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodCreateResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerViewFoods.setVisibility(View.VISIBLE);
                Log.e(TAG, "Error fetching foods", t);
                Toast.makeText(FoodListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}