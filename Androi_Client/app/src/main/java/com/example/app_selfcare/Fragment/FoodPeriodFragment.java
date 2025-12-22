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
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
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
    private BaseActivity baseActivity;

    // ===== FACTORY =====
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
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
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
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_recipe_period, container, false);

        initViews(view);
        setupRecyclerView();
        initApiService();
        loadFoodsFromApi();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

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

    private void loadFoodsFromApi() {

        if (baseActivity != null) {
            baseActivity.showLoading();
        }

        Call<ApiResponse<List<FoodResponse>>> call =
                (mealType == null || "ALL".equals(mealType))
                        ? apiService.getAllFoods()
                        : apiService.getFoodsByMealType(mealType);

        call.enqueue(new Callback<ApiResponse<List<FoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodResponse>>> call,
                                   Response<ApiResponse<List<FoodResponse>>> response) {

                if (baseActivity != null) {
                    baseActivity.hideLoading();
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getCode() == 200
                        && response.body().getResult() != null) {

                    List<Food> foods = convert(response.body().getResult());

                    if (foods.isEmpty()) {
                        showEmpty();
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setFoodList(foods);
                    }

                } else {
                    showEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodResponse>>> call, Throwable t) {
                if (baseActivity != null) {
                    baseActivity.hideLoading();
                }
                showEmpty();
                Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
                Log.e("FoodPeriodFragment", "API error", t);
            }
        });
    }

    private List<Food> convert(List<FoodResponse> responses) {
        List<Food> list = new ArrayList<>();
        for (FoodResponse r : responses) {
            list.add(r.toFood());
        }
        return list;
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}
