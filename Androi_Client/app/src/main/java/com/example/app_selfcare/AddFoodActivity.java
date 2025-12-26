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

/**
 * AddFoodActivity - Màn hình thêm/sửa món ăn (Admin)
 * 
 * Chức năng chính:
 * - Thêm món ăn mới với đầy đủ thông tin dinh dưỡng
 * - Chỉnh sửa món ăn đã có (edit mode)
 * - Upload hình ảnh món ăn
 * - Chọn danh mục, loại bữa ăn, độ khó, mục tiêu sức khỏe
 * - Thêm danh mục mới trực tiếp từ màn hình này
 */
public class AddFoodActivity extends AppCompatActivity {

    private static final String TAG = "AddFoodActivity";

    // Views - Thông tin cơ bản
    private TextInputEditText etFoodName, etCalories, etProtein, etFat, etFiber, etSugar;
    private TextInputEditText etInstructions, etPrepTime, etCookTime, etServings;
    
    // Views - Dropdown selections
    private MaterialAutoCompleteTextView actvCategory, actvMealType, actvDifficulty, actvGoal;
    
    // Views - Hình ảnh và loading
    private ImageView ivFoodImage;
    private ProgressBar progressBar;

    // Data
    private List<FoodCategoryResponse> categoryList = new ArrayList<>();
    private FoodCategoryResponse selectedCategory;
    private Uri selectedImageUri;
    private ApiService apiService;

    // Edit mode - true nếu đang sửa món ăn, false nếu thêm mới
    private boolean isEditMode = false;
    private FoodResponse editingFood;

    /**
     * Launcher để chọn ảnh từ gallery
     * Sử dụng ActivityResultContracts thay cho startActivityForResult (deprecated)
     */
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Hiển thị ảnh đã chọn
                    Glide.with(this).load(selectedImageUri).centerCrop().into(ivFoodImage);
                }
            }
    );

    /**
     * Khởi tạo Activity
     * - Thiết lập giao diện
     * - Load danh sách categories
     * - Kiểm tra edit mode
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);

        initViews();
        setupDropdowns();
        setupClickListeners();
        loadCategories();

        // Kiểm tra nếu đang ở chế độ sửa món ăn
        if (getIntent().hasExtra("food")) {
            isEditMode = true;
            editingFood = (FoodResponse) getIntent().getSerializableExtra("food");
            populateEditData();
        }
    }

    /**
     * Ánh xạ các view từ layout
     */
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

    /**
     * Thiết lập các sự kiện click
     * - Nút back
     * - Chọn ảnh
     * - Thêm danh mục
     * - Lưu món ăn
     */
    private void setupClickListeners() {
        // Nút quay lại
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Click vào ảnh để chọn ảnh mới
        ivFoodImage.setOnClickListener(v -> openImagePicker());

        // Nút thêm danh mục mới
        findViewById(R.id.btnAddCategory).setOnClickListener(v -> showAddCategoryDialog());

        // Nút lưu món ăn
        findViewById(R.id.btnSaveFood).setOnClickListener(v -> saveFood());
    }

    /**
     * Mở gallery để chọn ảnh món ăn
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Load danh sách danh mục món ăn từ API
     * Hiển thị trong dropdown để user chọn
     */
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

    /**
     * Thiết lập dropdown danh mục với dữ liệu đã load
     * Cho phép click để hiển thị danh sách và chọn
     */
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

    /**
     * Hiển thị dialog thêm danh mục mới
     * Sau khi thêm thành công, cập nhật dropdown và chọn danh mục vừa tạo
     */
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

    /**
     * Điền dữ liệu món ăn vào form khi ở chế độ sửa
     * Lấy dữ liệu từ editingFood và set vào các view
     */
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

    /**
     * Lưu món ăn (thêm mới hoặc cập nhật)
     * - Validate các trường bắt buộc
     * - Build request với multipart (có thể có ảnh)
     * - Gọi API tương ứng (create hoặc update)
     */
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

    /**
     * Callback xử lý response khi lưu món ăn
     * @param successMsg Thông báo hiển thị khi thành công
     * @return Callback object
     */
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

    /**
     * Hiển thị/ẩn loading indicator
     * @param show true để hiển thị, false để ẩn
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Lấy text từ EditText, trả về chuỗi rỗng nếu null
     * @param et TextInputEditText cần lấy giá trị
     * @return Chuỗi text đã trim
     */
    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    /**
     * Lấy text từ EditText, trả về "0" nếu rỗng
     * Dùng cho các trường số
     * @param et TextInputEditText cần lấy giá trị
     * @return Chuỗi text hoặc "0"
     */
    private String getTextOrZero(TextInputEditText et) {
        String text = getText(et);
        return TextUtils.isEmpty(text) ? "0" : text;
    }

    /**
     * Lấy text từ EditText, trả về giá trị mặc định nếu rỗng
     * @param et TextInputEditText cần lấy giá trị
     * @param defaultVal Giá trị mặc định
     * @return Chuỗi text hoặc defaultVal
     */
    private String getTextOrDefault(TextInputEditText et, String defaultVal) {
        String text = getText(et);
        return TextUtils.isEmpty(text) ? defaultVal : text;
    }

    /**
     * Chuyển đổi String thành RequestBody cho multipart request
     * @param value Giá trị cần chuyển
     * @return RequestBody object
     */
    private RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }

    /**
     * Chuyển đổi Uri thành File để upload
     * Copy nội dung từ Uri vào file tạm trong cache
     * @param uri Uri của ảnh đã chọn
     * @return File object
     * @throws Exception Nếu có lỗi khi đọc/ghi file
     */
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
