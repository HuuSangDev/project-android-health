package com.example.app_selfcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.SearchAdapter;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCreateResponse;
import com.example.app_selfcare.Data.Model.SearchItem;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.FoodDetailActivity;
import com.example.app_selfcare.R;
import com.example.app_selfcare.WorkoutDetailActivity;
import com.example.app_selfcare.utils.SearchHistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    
    private EditText etSearch;
    private ImageView btnBack, btnClearSearch;
    private RecyclerView rvSuggestions;
    private LinearLayout layoutEmptyState, layoutSuggestions;
    private ProgressBar progressBar;

    private SearchAdapter adapter;
    private final List<SearchItem> allItems = new ArrayList<>();
    private final List<SearchItem> filteredItems = new ArrayList<>();
    
    // Maps để lưu thông tin chi tiết cho navigation
    private final Map<Integer, FoodCreateResponse> foodDetailsMap = new HashMap<>();
    private final Map<Integer, ExerciseResponse> exerciseDetailsMap = new HashMap<>();
    
    private ApiService apiService;
    private SearchHistoryManager historyManager;
    private boolean isShowingHistory = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupRecyclerView();
        setupEvents();
        loadDataFromAPI();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearchQuery);
        btnBack = view.findViewById(R.id.btnBack);
        btnClearSearch = view.findViewById(R.id.btnClearSearch);
        rvSuggestions = view.findViewById(R.id.rvSuggestions);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        layoutSuggestions = view.findViewById(R.id.layoutSuggestions);
        
        // Tạo ProgressBar programmatically nếu chưa có trong layout
        progressBar = new ProgressBar(getContext());
        
        // Khởi tạo SearchHistoryManager
        historyManager = new SearchHistoryManager(getContext());
        
        // Kiểm tra token trước khi tạo API service
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("APP_DATA", android.content.Context.MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);
        Log.d(TAG, "Current token: " + (token != null ? "EXISTS (length: " + token.length() + ")" : "NULL"));
        
        // Sử dụng client với token để authentication
        apiService = ApiClient.getClientWithToken(getContext()).create(ApiService.class);
    }

    private void setupRecyclerView() {
        adapter = new SearchAdapter(filteredItems, item -> {
            // Lưu vào lịch sử khi click
            saveToHistory(item);
            
            // Xử lý click item - mở detail activity
            if (item.getType() == SearchItem.TYPE_FOOD) {
                openFoodDetail(item.getId());
            } else if (item.getType() == SearchItem.TYPE_WORKOUT) {
                openExerciseDetail(item.getId());
            }
            
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSuggestions.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );
        
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                
                if (query.isEmpty()) {
                    btnClearSearch.setVisibility(View.GONE);
                    showHistory();
                } else {
                    btnClearSearch.setVisibility(View.VISIBLE);
                    filterItems(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDataFromAPI() {
        Log.d(TAG, "Starting to load data from API...");
        showLoading(true);
        
        // Load Foods
        loadFoods();
        
        // Load Exercises  
        loadExercises();
        
        // Hiển thị lịch sử ngay lập tức nếu có
        if (historyManager.hasHistory()) {
            showHistory();
        }
        
        // Fallback: Tạo dữ liệu test sau 3 giây nếu không có dữ liệu
        new android.os.Handler().postDelayed(() -> {
            if (allItems.isEmpty()) {
                Log.w(TAG, "No data loaded from API, creating test data");
                createTestData();
            }
        }, 3000);
    }

    private void createTestData() {
        Log.d(TAG, "Creating test data...");
        
        // Test foods
        allItems.add(new SearchItem(1, "Salad trứng luộc", SearchItem.TYPE_FOOD));
        allItems.add(new SearchItem(2, "Ức gà áp chảo", SearchItem.TYPE_FOOD));
        allItems.add(new SearchItem(3, "Cơm gạo lứt", SearchItem.TYPE_FOOD));
        
        // Test exercises
        allItems.add(new SearchItem(4, "Push-up", SearchItem.TYPE_WORKOUT));
        allItems.add(new SearchItem(5, "Squat", SearchItem.TYPE_WORKOUT));
        allItems.add(new SearchItem(6, "Plank", SearchItem.TYPE_WORKOUT));
        
        Log.d(TAG, "Created " + allItems.size() + " test items");
        showAllItems();
        showLoading(false);
    }

    private void loadFoods() {
        Log.d(TAG, "Loading foods from API...");
        apiService.getAllFoodsForSearch().enqueue(new Callback<ApiResponse<List<FoodCreateResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodCreateResponse>>> call, 
                                 Response<ApiResponse<List<FoodCreateResponse>>> response) {
                Log.d(TAG, "Foods API response received. Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<FoodCreateResponse>> apiResponse = response.body();
                    Log.d(TAG, "API Response code: " + apiResponse.getCode());
                    Log.d(TAG, "API Response message: " + apiResponse.getMessage());
                    
                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        List<FoodCreateResponse> foods = apiResponse.getResult();
                        Log.d(TAG, "Received " + foods.size() + " foods from API");
                        
                        for (FoodCreateResponse food : foods) {
                            SearchItem item = new SearchItem(
                                food.getFoodIdAsInt(),
                                food.getFoodName(),
                                SearchItem.TYPE_FOOD,
                                food.getImageUrl()
                            );
                            allItems.add(item);
                            
                            // Lưu thông tin chi tiết để navigation
                            foodDetailsMap.put(food.getFoodIdAsInt(), food);
                            
                            Log.d(TAG, "Added food: " + food.getFoodName());
                        }
                        
                        updateUI();
                        Log.d(TAG, "Total items after loading foods: " + allItems.size());
                    } else {
                        Log.e(TAG, "API returned error code: " + apiResponse.getCode());
                    }
                } else {
                    Log.e(TAG, "Failed to load foods. Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodCreateResponse>>> call, Throwable t) {
                Log.e(TAG, "Error loading foods", t);
                showError("Không thể tải dữ liệu món ăn: " + t.getMessage());
            }
        });
    }

    private void loadExercises() {
        apiService.getExercises().enqueue(new Callback<ApiResponse<List<ExerciseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExerciseResponse>>> call,
                                 Response<ApiResponse<List<ExerciseResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ExerciseResponse>> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        List<ExerciseResponse> exercises = apiResponse.getResult();
                        
                        for (ExerciseResponse exercise : exercises) {
                            int exerciseId = exercise.getExerciseId() != null ? exercise.getExerciseId().intValue() : 0;
                            SearchItem item = new SearchItem(
                                exerciseId,
                                exercise.getExerciseName(),
                                SearchItem.TYPE_WORKOUT,
                                exercise.getImageUrl()
                            );
                            allItems.add(item);
                            
                            // Lưu thông tin chi tiết để navigation
                            exerciseDetailsMap.put(exerciseId, exercise);
                        }
                        
                        updateUI();
                        Log.d(TAG, "Loaded " + exercises.size() + " exercises");
                    }
                } else {
                    Log.e(TAG, "Failed to load exercises: " + response.code());
                }
                
                showLoading(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExerciseResponse>>> call, Throwable t) {
                Log.e(TAG, "Error loading exercises", t);
                showError("Không thể tải dữ liệu bài tập");
                showLoading(false);
            }
        });
    }

    private void filterItems(String query) {
        filteredItems.clear();
        
        String lowerQuery = query.toLowerCase();
        for (SearchItem item : allItems) {
            if (item.getName().toLowerCase().contains(lowerQuery)) {
                filteredItems.add(item);
            }
        }
        
        updateUI();
    }

    private void showAllItems() {
        Log.d(TAG, "showAllItems called. All items size: " + allItems.size());
        filteredItems.clear();
        filteredItems.addAll(allItems);
        Log.d(TAG, "Filtered items after showAllItems: " + filteredItems.size());
        updateUI();
    }

    private void updateUI() {
        if (getActivity() == null) {
            Log.w(TAG, "Activity is null, cannot update UI");
            return;
        }
        
        Log.d(TAG, "Updating UI. Filtered items count: " + filteredItems.size());
        Log.d(TAG, "All items count: " + allItems.size());
        
        getActivity().runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            
            if (filteredItems.isEmpty()) {
                Log.d(TAG, "Showing empty state");
                layoutEmptyState.setVisibility(View.VISIBLE);
                layoutSuggestions.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Showing suggestions with " + filteredItems.size() + " items");
                layoutEmptyState.setVisibility(View.GONE);
                layoutSuggestions.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showLoading(boolean show) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            if (show) {
                layoutEmptyState.setVisibility(View.GONE);
                layoutSuggestions.setVisibility(View.GONE);
            }
        });
    }

    private void showError(String message) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void openFoodDetail(int foodId) {
        try {
            // Import Intent và FoodDetailActivity
            android.content.Intent intent = new android.content.Intent(getContext(), 
                com.example.app_selfcare.FoodDetailActivity.class);
            intent.putExtra("FOOD_ID", foodId);
            startActivity(intent);
            Log.d(TAG, "Opening food detail for ID: " + foodId);
        } catch (Exception e) {
            Log.e(TAG, "Error opening food detail", e);
            Toast.makeText(getContext(), "Không thể mở chi tiết món ăn", Toast.LENGTH_SHORT).show();
        }
    }

    private void openExerciseDetail(int exerciseId) {
        try {
            // Import Intent và WorkoutDetailActivity
            android.content.Intent intent = new android.content.Intent(getContext(), 
                com.example.app_selfcare.WorkoutDetailActivity.class);
            intent.putExtra("EXERCISE_ID", exerciseId);
            startActivity(intent);
            Log.d(TAG, "Opening exercise detail for ID: " + exerciseId);
        } catch (Exception e) {
            Log.e(TAG, "Error opening exercise detail", e);
            Toast.makeText(getContext(), "Không thể mở chi tiết bài tập", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lưu item vào lịch sử tìm kiếm
     */
    private void saveToHistory(SearchItem item) {
        if (historyManager != null) {
            String imageUrl = item.getImageUrl() != null ? item.getImageUrl() : "";
            String category = item.getType() == SearchItem.TYPE_FOOD ? "Món ăn" : "Bài tập";
            historyManager.addToHistory(item, imageUrl, category);
            Log.d(TAG, "Saved to history: " + item.getName());
        }
    }

    /**
     * Hiển thị lịch sử tìm kiếm
     */
    private void showHistory() {
        if (historyManager != null && historyManager.hasHistory()) {
            isShowingHistory = true;
            filteredItems.clear();
            filteredItems.addAll(historyManager.getHistoryAsSearchItems());
            updateUI();
            Log.d(TAG, "Showing history with " + filteredItems.size() + " items");
        } else {
            isShowingHistory = false;
            showAllItems();
        }
    }
}
