package com.example.app_selfcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCategoryResponse;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFoodActivity extends AppCompatActivity {

    private static final String TAG = "AddFoodActivity";

    // Views
    private TextInputEditText etFoodName, etCalories, etProtein, etFat, etFiber, etSugar;
    private TextInputEditText etInstructions, etPrepTime, etCookTime, etServings;
    private MaterialAutoCompleteTextView actvCategory, actvMealType, actvDifficulty, actvGoal;
    private ImageView ivFoodImage;
    private ProgressBar progressBar;

    // Data
    private List<FoodCategoryResponse> categoryList = new ArrayList<>();
    private FoodCategoryResponse selectedCategory;
    private Uri selectedImageUri;
    private ApiService apiService;

    // Edit mode
    private boolean isEditMode = false;
    private FoodResponse editingFood;

    // Image picker
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).centerCrop().into(ivFoodImage);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);

        initViews();
        setupDropdowns();
        setupClickListeners();
        loadCategories();

        // Check edit mode
        if (getIntent().hasExtra("food")) {
            isEditMode = true;
            editingFood = (FoodResponse) getIntent().getSerializableExtra("food");
            populateEditData();
        }
    }

    private void initViews() {
        etFoodName = findViewById(R.id.etFoodName);
        etCalories = findViewById(R.id.etCalories);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        etFiber = findViewById(R.id.etFiber);
        etSugar = findViewById(R.id.etSugar);
        etInstructions = findViewById(R.id.etInstructions);
        etPrepTime = findViewById(R.id.etPrepTime);
        etCookTime = findViewById(R.id.etCookTime);
        etServings = findViewById(R.id.etServings);

        actvCategory = findViewById(R.id.actvCategory);
        actvMealType = findViewById(R.id.actvMealType);
        actvDifficulty = findViewById(R.id.actvDifficulty);
        actvGoal = findViewById(R.id.actvGoal);

        ivFoodImage = findViewById(R.id.ivFoodImage);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupDropdowns() {
        // MealType dropdown
        String[] mealTypes = {"BREAKFAST", "LUNCH", "DINNER", "ALL"};
        ArrayAdapter<String> mealAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, mealTypes);
        actvMealType.setAdapter(mealAdapter);
        actvMealType.setOnClickListener(v -> actvMealType.showDropDown());

        // Difficulty dropdown
        String[] difficulties = {"EASY", "MEDIUM", "HARD"};
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, difficulties);
        actvDifficulty.setAdapter(diffAdapter);
        actvDifficulty.setOnClickListener(v -> actvDifficulty.showDropDown());

        // Goal dropdown
        String[] goals = {"WEIGHT_LOSS", "MAINTAIN", "WEIGHT_GAIN"};
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, goals);
        actvGoal.setAdapter(goalAdapter);
        actvGoal.setOnClickListener(v -> actvGoal.showDropDown());
    }

    private void setupClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        ivFoodImage.setOnClickListener(v -> openImagePicker());

        findViewById(R.id.btnAddCategory).setOnClickListener(v -> showAddCategoryDialog());

        findViewById(R.id.btnSaveFood).setOnClickListener(v -> saveFood());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void loadCategories() {
        Log.d(TAG, "Loading categories...");
        Toast.makeText(this, "Đang tải danh mục...", Toast.LENGTH_SHORT).show();
        
        apiService.getAllFoodCategories().enqueue(new Callback<ApiResponse<List<FoodCategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FoodCategoryResponse>>> call,
                                   Response<ApiResponse<List<FoodCategoryResponse>>> response) {
                Log.d(TAG, "Categories response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Categories API code: " + response.body().getCode());
                    if (response.body().getResult() != null) {
                        categoryList.clear();
                        categoryList.addAll(response.body().getResult());
                        Log.d(TAG, "Loaded " + categoryList.size() + " categories");
                        Toast.makeText(AddFoodActivity.this, 
                                "Đã tải " + categoryList.size() + " danh mục", Toast.LENGTH_SHORT).show();
                        
                        for (FoodCategoryResponse cat : categoryList) {
                            Log.d(TAG, "Category: " + cat.getCategoryId() + " - " + cat.getCategoryName());
                        }
                        setupCategoryDropdown();
                    } else {
                        Log.e(TAG, "Categories result is null");
                        Toast.makeText(AddFoodActivity.this, "Danh mục rỗng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Categories response failed: " + response.code());
                    Toast.makeText(AddFoodActivity.this, 
                            "Lỗi API: " + response.code(), Toast.LENGTH_SHORT).show();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FoodCategoryResponse>>> call, Throwable t) {
                Log.e(TAG, "Load categories failed", t);
                Toast.makeText(AddFoodActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupCategoryDropdown() {
        Log.d(TAG, "Setting up category dropdown with " + categoryList.size() + " items");
        
        // Tạo list tên categories để hiển thị
        List<String> categoryNames = new ArrayList<>();
        for (FoodCategoryResponse cat : categoryList) {
            categoryNames.add(cat.getCategoryName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        actvCategory.setAdapter(adapter);
        actvCategory.setThreshold(0); // Hiển thị tất cả khi click
        
        // Cho phép click để hiển thị dropdown
        actvCategory.setOnClickListener(v -> {
            actvCategory.showDropDown();
        });
        
        actvCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && categoryList.size() > 0) {
                actvCategory.showDropDown();
            }
        });

        actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = categoryList.get(position);
            Log.d(TAG, "Selected category: " + selectedCategory.getCategoryName());
        });

        // Set selected category in edit mode
        if (isEditMode && editingFood != null && editingFood.getCategoryResponse() != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCategoryId() == editingFood.getCategoryResponse().getCategoryId()) {
                    selectedCategory = categoryList.get(i);
                    actvCategory.setText(selectedCategory.getCategoryName(), false);
                    break;
                }
            }
        }
    }

    private void showAddCategoryDialog() {
        AddFoodCategoryDialog dialog = new AddFoodCategoryDialog();
        dialog.setListener(category -> {
            categoryList.add(category);
            setupCategoryDropdown();
            selectedCategory = category;
            actvCategory.setText(category.getCategoryName(), false);
        });
        dialog.show(getSupportFragmentManager(), "AddFoodCategoryDialog");
    }

    private void populateEditData() {
        if (editingFood == null) return;

        etFoodName.setText(editingFood.getFoodName());
        etCalories.setText(String.valueOf(editingFood.getCaloriesPer100g()));
        etProtein.setText(String.valueOf(editingFood.getProteinPer100g()));
        etFat.setText(String.valueOf(editingFood.getFatPer100g()));
        etFiber.setText(String.valueOf(editingFood.getFiberPer100g()));
        etSugar.setText(String.valueOf(editingFood.getSugarPer100g()));
        etInstructions.setText(editingFood.getInstructions());
        etPrepTime.setText(String.valueOf(editingFood.getPrepTime()));
        etCookTime.setText(String.valueOf(editingFood.getCookTime()));
        etServings.setText(String.valueOf(editingFood.getServings()));

        actvMealType.setText(editingFood.getMealType(), false);
        actvDifficulty.setText(editingFood.getDifficultyLevel(), false);
        actvGoal.setText(editingFood.getGoal(), false);

        if (editingFood.getImageUrl() != null) {
            Glide.with(this).load(editingFood.getImageUrl()).centerCrop().into(ivFoodImage);
        }
    }

    private void saveFood() {
        // Validate
        String foodName = getText(etFoodName);
        String caloriesStr = getText(etCalories);
        String goal = actvGoal.getText().toString().trim();

        if (TextUtils.isEmpty(foodName)) {
            etFoodName.setError("Nhập tên món ăn");
            return;
        }
        if (TextUtils.isEmpty(caloriesStr)) {
            etCalories.setError("Nhập calories");
            return;
        }
        if (TextUtils.isEmpty(goal)) {
            Toast.makeText(this, "Chọn mục tiêu", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        // Build request parts
        RequestBody rbFoodName = toRequestBody(foodName);
        RequestBody rbCalories = toRequestBody(caloriesStr);
        RequestBody rbProtein = toRequestBody(getTextOrZero(etProtein));
        RequestBody rbFat = toRequestBody(getTextOrZero(etFat));
        RequestBody rbFiber = toRequestBody(getTextOrZero(etFiber));
        RequestBody rbSugar = toRequestBody(getTextOrZero(etSugar));
        RequestBody rbInstructions = toRequestBody(getText(etInstructions));
        RequestBody rbPrepTime = toRequestBody(getTextOrZero(etPrepTime));
        RequestBody rbCookTime = toRequestBody(getTextOrZero(etCookTime));
        RequestBody rbServings = toRequestBody(getTextOrDefault(etServings, "1"));
        RequestBody rbMealType = toRequestBody(actvMealType.getText().toString().trim());
        RequestBody rbDifficulty = toRequestBody(actvDifficulty.getText().toString().trim());
        RequestBody rbCategoryId = toRequestBody(selectedCategory != null ? 
                String.valueOf(selectedCategory.getCategoryId()) : "");
        RequestBody rbGoal = toRequestBody(goal);

        // Image part
        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            try {
                File imageFile = getFileFromUri(selectedImageUri);
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageBody);
            } catch (Exception e) {
                Log.e(TAG, "Error processing image", e);
            }
        }

        if (isEditMode && editingFood != null) {
            // Update
            apiService.updateFood(editingFood.getFoodId(),
                    rbFoodName, rbCalories, rbProtein, rbFat, rbFiber, rbSugar,
                    rbInstructions, rbPrepTime, rbCookTime, rbServings,
                    rbMealType, rbDifficulty, rbCategoryId, rbGoal, imagePart
            ).enqueue(foodCallback("Cập nhật món ăn thành công"));
        } else {
            // Create
            apiService.createFood(
                    rbFoodName, rbCalories, rbProtein, rbFat, rbFiber, rbSugar,
                    rbInstructions, rbPrepTime, rbCookTime, rbServings,
                    rbMealType, rbDifficulty, rbCategoryId, rbGoal, imagePart
            ).enqueue(foodCallback("Thêm món ăn thành công"));
        }
    }

    private Callback<ApiResponse<FoodResponse>> foodCallback(String successMsg) {
        return new Callback<ApiResponse<FoodResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodResponse>> call,
                                   Response<ApiResponse<FoodResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddFoodActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddFoodActivity.this, 
                            "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodResponse>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Save food failed", t);
                Toast.makeText(AddFoodActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private String getTextOrZero(TextInputEditText et) {
        String text = getText(et);
        return TextUtils.isEmpty(text) ? "0" : text;
    }

    private String getTextOrDefault(TextInputEditText et, String defaultVal) {
        String text = getText(et);
        return TextUtils.isEmpty(text) ? defaultVal : text;
    }

    private RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }

    private File getFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("food_image", ".jpg", getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
        return tempFile;
    }
}
