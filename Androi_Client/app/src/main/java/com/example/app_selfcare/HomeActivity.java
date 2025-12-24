package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Adapter.FoodSliderAdapter;
import com.example.app_selfcare.Adapter.WorkoutSliderAdapter;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.Model.Response.UserProfileResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.Fragment.SearchFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private FrameLayout fragmentContainer;
    private ViewPager2 vpWorkouts, vpFoods;
    private TextView tvDate, tvUserName, tvBMIStatus;
    private ImageView ivUserAvatar;
    
    private WorkoutSliderAdapter workoutAdapter;
    private FoodSliderAdapter foodAdapter;
    
    private Handler autoSlideHandler;
    private Runnable workoutSlideRunnable, foodSlideRunnable;
    
    private static final int AUTO_SLIDE_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupClickListeners();
        
        showLoading();
        loadUserProfile();
        loadWorkouts();
        loadFoods();
    }

    private void initViews() {
        fragmentContainer = findViewById(R.id.fragmentContainer);
        vpWorkouts = findViewById(R.id.vpWorkouts);
        vpFoods = findViewById(R.id.vpFoods);
        tvDate = findViewById(R.id.tvDate);
        tvUserName = findViewById(R.id.tvUserName);
        tvBMIStatus = findViewById(R.id.tvBMIStatus);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        
        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'tháng' MM, yyyy", new Locale("vi", "VN"));
        tvDate.setText(sdf.format(new Date()));
        
        autoSlideHandler = new Handler(Looper.getMainLooper());
    }

    private String getToken() {
        return getSharedPreferences("APP_DATA", MODE_PRIVATE)
                .getString("TOKEN", "");
    }

    private void loadUserProfile() {
        ApiService apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
        Call<ApiResponse<UserResponse>> call = apiService.getUserProfile("Bearer " + getToken());
        
        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    UserResponse user = response.body().getResult();
                    updateUserInfo(user);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e("HomeActivity", "Failed to load user profile", t);
            }
        });
    }

    private void updateUserInfo(UserResponse user) {
        tvUserName.setText("Xin chào, " + user.getFullName());
        
        // Load avatar
        UserProfileResponse profile = user.getUserProfileResponse();
        if (profile != null && profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.avatar1)
                    .error(R.drawable.avatar1)
                    .circleCrop()
                    .into(ivUserAvatar);
        }
        
        // Calculate and display BMI
        if (profile != null && profile.getWeight() != null && profile.getHeight() != null && 
            profile.getWeight() > 0 && profile.getHeight() > 0) {
            double heightInM = profile.getHeight() / 100.0;
            double bmi = profile.getWeight() / (heightInM * heightInM);
            updateBMIStatus(bmi);
        }
    }

    private void updateBMIStatus(double bmi) {
        String status;
        int color;
        
        if (bmi < 18.5) {
            status = "BMI: " + String.format("%.1f", bmi) + " - Thiếu cân";
            color = getResources().getColor(android.R.color.holo_orange_light);
        } else if (bmi < 25) {
            status = "BMI: " + String.format("%.1f", bmi) + " - Bình thường";
            color = getResources().getColor(android.R.color.holo_green_light);
        } else if (bmi < 30) {
            status = "BMI: " + String.format("%.1f", bmi) + " - Thừa cân";
            color = getResources().getColor(android.R.color.holo_red_light);
        } else {
            status = "BMI: " + String.format("%.1f", bmi) + " - Béo phì";
            color = getResources().getColor(android.R.color.holo_red_dark);
        }
        
        tvBMIStatus.setText(status);
        tvBMIStatus.setTextColor(color);
        tvBMIStatus.setVisibility(View.VISIBLE);
    }

    private void loadWorkouts() {
        ApiService apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
        Call<ApiResponse<List<ExerciseResponse>>> call = apiService.getExercises();
        
        call.enqueue(new Callback<ApiResponse<List<ExerciseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExerciseResponse>>> call, Response<ApiResponse<List<ExerciseResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    List<ExerciseResponse> exercises = response.body().getResult();
                    setupWorkoutSlider(exercises);
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExerciseResponse>>> call, Throwable t) {
                Log.e("HomeActivity", "Failed to load workouts", t);
                checkLoadingComplete();
            }
        });
    }

    private void loadFoods() {
        ApiService apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
        Call<ApiResponse<List<FoodResponse>>> call = apiService.getAllFoods();
        
        call.enqueue(new Callback<ApiResponse<List<FoodResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodResponse>>> call, Response<ApiResponse<List<FoodResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    List<FoodResponse> foods = response.body().getResult();
                    setupFoodSlider(foods);
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodResponse>>> call, Throwable t) {
                Log.e("HomeActivity", "Failed to load foods", t);
                checkLoadingComplete();
            }
        });
    }

    private void setupWorkoutSlider(List<ExerciseResponse> exercises) {
        if (exercises != null && !exercises.isEmpty()) {
            workoutAdapter = new WorkoutSliderAdapter(this, exercises);
            vpWorkouts.setAdapter(workoutAdapter);
            startWorkoutAutoSlide();
        }
    }

    private void setupFoodSlider(List<FoodResponse> foods) {
        if (foods != null && !foods.isEmpty()) {
            foodAdapter = new FoodSliderAdapter(this, foods);
            vpFoods.setAdapter(foodAdapter);
            startFoodAutoSlide();
        }
    }

    private void startWorkoutAutoSlide() {
        workoutSlideRunnable = new Runnable() {
            @Override
            public void run() {
                if (workoutAdapter != null && workoutAdapter.getItemCount() > 1) {
                    int currentItem = vpWorkouts.getCurrentItem();
                    int nextItem = (currentItem + 1) % workoutAdapter.getItemCount();
                    vpWorkouts.setCurrentItem(nextItem, true);
                }
                autoSlideHandler.postDelayed(this, AUTO_SLIDE_DELAY);
            }
        };
        autoSlideHandler.postDelayed(workoutSlideRunnable, AUTO_SLIDE_DELAY);
    }

    private void startFoodAutoSlide() {
        foodSlideRunnable = new Runnable() {
            @Override
            public void run() {
                if (foodAdapter != null && foodAdapter.getItemCount() > 1) {
                    int currentItem = vpFoods.getCurrentItem();
                    int nextItem = (currentItem + 1) % foodAdapter.getItemCount();
                    vpFoods.setCurrentItem(nextItem, true);
                }
                autoSlideHandler.postDelayed(this, AUTO_SLIDE_DELAY);
            }
        };
        autoSlideHandler.postDelayed(foodSlideRunnable, AUTO_SLIDE_DELAY);
    }

    private void checkLoadingComplete() {
        // Hide loading when both API calls are done (success or failure)
        hideLoading();
    }

    private void setupClickListeners() {
        // SEARCH
        findViewById(R.id.layoutSearch).setOnClickListener(v -> openSearchFragment());

        // SEE ALL DIET
        findViewById(R.id.tvSeeAllDiet).setOnClickListener(v -> {
            startActivity(new Intent(this, RecipeHomeActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // SEE ALL WORKOUTS
        findViewById(R.id.tvSeeAllWorkouts).setOnClickListener(v -> {
            startActivity(new Intent(this, WorkoutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // BOTTOM NAV
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navWorkout).setOnClickListener(v ->
                startActivity(new Intent(this, WorkoutActivity.class)));

        findViewById(R.id.navPlanner).setOnClickListener(v ->
                startActivity(new Intent(this, RecipeHomeActivity.class)));

        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        findViewById(R.id.fabChat).setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));
    }

    private void openSearchFragment() {
        SearchFragment fragment = new SearchFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.fade_in_fast,
                0,
                0,
                R.anim.fade_out_fast
        );

        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack("SearchFragment");
        transaction.commit();

        fragmentContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop auto-slide when activity is destroyed
        if (autoSlideHandler != null) {
            if (workoutSlideRunnable != null) {
                autoSlideHandler.removeCallbacks(workoutSlideRunnable);
            }
            if (foodSlideRunnable != null) {
                autoSlideHandler.removeCallbacks(foodSlideRunnable);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause auto-slide when activity is not visible
        if (autoSlideHandler != null) {
            if (workoutSlideRunnable != null) {
                autoSlideHandler.removeCallbacks(workoutSlideRunnable);
            }
            if (foodSlideRunnable != null) {
                autoSlideHandler.removeCallbacks(foodSlideRunnable);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume auto-slide when activity becomes visible
        if (workoutAdapter != null && workoutAdapter.getItemCount() > 1) {
            startWorkoutAutoSlide();
        }
        if (foodAdapter != null && foodAdapter.getItemCount() > 1) {
            startFoodAutoSlide();
        }
    }
}
