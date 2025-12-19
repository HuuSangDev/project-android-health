package com.example.app_selfcare.Fragment;

import android.content.Context;
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
import com.example.app_selfcare.BaseActivity;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.LoadingHandler;
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
    private LoadingHandler loadingHandler;

    // ================== FACTORY ==================
    public static FoodPeriodFragment newInstance(String mealType) {
        FoodPeriodFragment fragment = new FoodPeriodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEAL_TYPE, mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoadingHandler) {
            loadingHandler = (LoadingHandler) context;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealType = getArguments().getString(ARG_MEAL_TYPE);
        }
    }

    // ================== VIEW ==================
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_recipe_period, container, false);

        initViews(view);
        setupRecyclerView();
        initApiService();
        loadFoodsFromApi(); // 👉 API THẬT + LOADING

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
                default:
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
        apiService = ApiClient
                .getClientWithToken(requireContext())
                .create(ApiService.class);
    }

    // ================== API ==================
    private void loadFoodsFromApi() {

        if (loadingHandler != null) {
            loadingHandler.showLoading("Đang tải món ăn...");
        }


        Call<ApiResponse<List<FoodResponse>>> call;

        if (mealType == null || "ALL".equals(mealType)) {
            call = apiService.getAllFoods();
        } else {
            call = apiService.getFoodsByMealType(mealType);
        }

        call.enqueue(new Callback<ApiResponse<List<FoodResponse>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<FoodResponse>>> call,
                    Response<ApiResponse<List<FoodResponse>>> response
            ) {
                if (loadingHandler != null) {
                    loadingHandler.hideLoading();
                }


                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<FoodResponse>> apiResponse = response.body();

                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        List<Food> foods = convertToFoodList(apiResponse.getResult());

                        if (foods.isEmpty()) {
                            showEmpty();
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                            adapter.setFoodList(foods);
                        }
                    } else {
                        showEmpty();
                        Log.e("FoodPeriodFragment", apiResponse.getMessage());
                    }
                } else {
                    showEmpty();
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<List<FoodResponse>>> call,
                    Throwable t
            ) {
                if (loadingHandler != null) {
                    loadingHandler.hideLoading();
                }


                showEmpty();
                Log.e("FoodPeriodFragment", "API error", t);
                Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== HELPER ==================
    private List<Food> convertToFoodList(List<FoodResponse> foodResponses) {
        List<Food> foods = new ArrayList<>();
        for (FoodResponse fr : foodResponses) {
            foods.add(fr.toFood());
        }
        return foods;
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}
