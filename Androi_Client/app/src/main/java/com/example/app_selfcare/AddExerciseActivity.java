package com.example.app_selfcare;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseCategoryResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AddExerciseActivity - Màn hình thêm/sửa bài tập (Admin)
 * 
 * Chức năng chính:
 * - Thêm bài tập mới với đầy đủ thông tin
 * - Chỉnh sửa bài tập đã có (edit mode)
 * - Upload hình ảnh và video hướng dẫn
 * - Chọn danh mục, độ khó, mục tiêu sức khỏe
 */
public class AddExerciseActivity extends AppCompatActivity {

    private static final String TAG = "AddExerciseActivity";

    // ===== Inputs =====
    private TextInputEditText etExerciseName;
    private TextInputEditText etCaloriesPerMinute;
    private TextInputEditText etDescription;
    private TextInputEditText etInstructions;
    private MaterialAutoCompleteTextView actvDifficultyLevel;
    private MaterialAutoCompleteTextView actvCategory;
    private MaterialAutoCompleteTextView actvGoal;

    private MaterialButton btnSaveExercise;
    private ProgressBar progressBar;

    // ===== Image picker UI =====
    private MaterialButton btnSelectImage;
    private FloatingActionButton btnRemoveImage;
    private View layoutImagePreview;
    private ImageView ivImagePreview;
    private TextView tvImageFileName;

    // ===== Video picker UI =====
    private MaterialButton btnSelectVideo;
    private MaterialButton btnRemoveVideo;
    private View layoutVideoPreview;
    private ImageView ivVideoThumbnail;
    private TextView tvVideoFileName, tvVideoDuration, tvVideoSize;

    // ===== Selected Uris =====
    private Uri imageUri = null;
    private Uri videoUri = null;

    // ===== Data =====
    private List<ExerciseCategoryResponse> categoryList = new ArrayList<>();
    private ExerciseCategoryResponse selectedCategory;
    private ApiService apiService;

    // ===== Edit mode =====
    private boolean isEditMode = false;
    private ExerciseResponse editingExercise;

    // ===== Pickers (Activity Result API) =====
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                imageUri = uri;
                showImageInfo(uri);
            });

    private final ActivityResultLauncher<String> pickVideoLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                videoUri = uri;
                showVideoInfo(uri);
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        apiService = ApiClient.getClientWithToken(this).create(ApiService.class);

        initViews();
        setupDropdowns();
        setupToolbar();
        setupClickListeners();
        loadCategories();

        // Check edit mode
        if (getIntent().hasExtra("exercise")) {
            isEditMode = true;
            editingExercise = (ExerciseResponse) getIntent().getSerializableExtra("exercise");
            populateEditData();
        }
    }

    /**
     * Ánh xạ các view từ layout
     */
    private void initViews() {
        etExerciseName = findViewById(R.id.etExerciseName);
        etCaloriesPerMinute = findViewById(R.id.etCaloriesPerMinute);
        etDescription = findViewById(R.id.etDescription);
        etInstructions = findViewById(R.id.etInstructions);
        actvDifficultyLevel = findViewById(R.id.actvDifficultyLevel);
        actvCategory = findViewById(R.id.actvCategory);
        actvGoal = findViewById(R.id.actvGoal);

        btnSaveExercise = findViewById(R.id.btnSaveExercise);
        progressBar = findViewById(R.id.progressBar);

        // Image
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        ivImagePreview = findViewById(R.id.ivImagePreview);
        tvImageFileName = findViewById(R.id.tvImageFileName);

        // Video
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnRemoveVideo = findViewById(R.id.btnRemoveVideo);
        layoutVideoPreview = findViewById(R.id.layoutVideoPreview);
        ivVideoThumbnail = findViewById(R.id.ivVideoThumbnail);
        tvVideoFileName = findViewById(R.id.tvVideoFileName);
        tvVideoDuration = findViewById(R.id.tvVideoDuration);
        tvVideoSize = findViewById(R.id.tvVideoSize);

        // init state
        layoutImagePreview.setVisibility(View.GONE);
        layoutVideoPreview.setVisibility(View.GONE);
    }

    /**
     * Thiết lập các dropdown (Độ khó, Mục tiêu)
     */
    private void setupDropdowns() {
        // Difficulty dropdown
        String[] difficulties = {"BEGINNER", "INTERMEDIATE", "ADVANCED"};
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, difficulties);
        actvDifficultyLevel.setAdapter(diffAdapter);
        actvDifficultyLevel.setOnClickListener(v -> actvDifficultyLevel.showDropDown());

        // Goal dropdown
        String[] goals = {"WEIGHT_LOSS", "MAINTAIN", "WEIGHT_GAIN"};
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, goals);
        actvGoal.setAdapter(goalAdapter);
        actvGoal.setOnClickListener(v -> actvGoal.showDropDown());
    }

    /**
     * Thiết lập toolbar với nút back
     */
    private void setupToolbar() {
        View back = findViewById(R.id.backButton);
        if (back != null) back.setOnClickListener(v -> finish());
    }

    /**
     * Thiết lập các sự kiện click
     */
    private void setupClickListeners() {
        // chọn ảnh/video
        btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSelectVideo.setOnClickListener(v -> pickVideoLauncher.launch("video/*"));

        // xoá ảnh/video
        btnRemoveImage.setOnClickListener(v -> clearImage());
        btnRemoveVideo.setOnClickListener(v -> clearVideo());

        // Thêm danh mục mới
        findViewById(R.id.btnAddCategory).setOnClickListener(v -> showAddCategoryDialog());

        // lưu
        btnSaveExercise.setOnClickListener(v -> validateAndSave());
    }

    /**
     * Load danh sách danh mục bài tập từ API
     */
    private void loadCategories() {
        Log.d(TAG, "Loading exercise categories...");
        
        apiService.getAllExerciseCategories().enqueue(new Callback<ApiResponse<List<ExerciseCategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExerciseCategoryResponse>>> call,
                                   Response<ApiResponse<List<ExerciseCategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getResult());
                    Log.d(TAG, "Loaded " + categoryList.size() + " categories");
                    setupCategoryDropdown();
                } else {
                    Log.e(TAG, "Categories response failed: " + response.code());
                    Toast.makeText(AddExerciseActivity.this, 
                            "Lỗi tải danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExerciseCategoryResponse>>> call, Throwable t) {
                Log.e(TAG, "Load categories failed", t);
                Toast.makeText(AddExerciseActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Thiết lập dropdown danh mục với dữ liệu đã load
     */
    private void setupCategoryDropdown() {
        List<String> categoryNames = new ArrayList<>();
        for (ExerciseCategoryResponse cat : categoryList) {
            categoryNames.add(cat.getCategoryName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        actvCategory.setAdapter(adapter);
        actvCategory.setThreshold(0);
        
        actvCategory.setOnClickListener(v -> actvCategory.showDropDown());
        
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
        if (isEditMode && editingExercise != null && editingExercise.getCategory() != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCategoryId() == editingExercise.getCategory().getCategoryId()) {
                    selectedCategory = categoryList.get(i);
                    actvCategory.setText(selectedCategory.getCategoryName(), false);
                    break;
                }
            }
        }
    }

    /**
     * Hiển thị dialog thêm danh mục mới
     */
    private void showAddCategoryDialog() {
        AddExerciseCategoryDialog dialog = new AddExerciseCategoryDialog();
        dialog.setListener(category -> {
            categoryList.add(category);
            setupCategoryDropdown();
            selectedCategory = category;
            actvCategory.setText(category.getCategoryName(), false);
        });
        dialog.show(getSupportFragmentManager(), "AddExerciseCategoryDialog");
    }

    /**
     * Điền dữ liệu bài tập vào form khi ở chế độ sửa
     */
    private void populateEditData() {
        if (editingExercise == null) return;

        etExerciseName.setText(editingExercise.getExerciseName());
        etCaloriesPerMinute.setText(String.valueOf(editingExercise.getCaloriesPerMinute()));
        etDescription.setText(editingExercise.getDescription());
        etInstructions.setText(editingExercise.getInstructions());

        actvDifficultyLevel.setText(editingExercise.getDifficultyLevel(), false);
        actvGoal.setText(editingExercise.getGoal(), false);

        // Load image if exists
        if (editingExercise.getImageUrl() != null) {
            // TODO: Load image from URL
        }
    }

    /**
     * Validate và lưu bài tập
     */
    private void validateAndSave() {
        String name = safeText(etExerciseName);
        String caloriesStr = safeText(etCaloriesPerMinute);
        String desc = safeText(etDescription);
        String instructions = safeText(etInstructions);
        String level = actvDifficultyLevel.getText() != null ? actvDifficultyLevel.getText().toString().trim() : "";
        String goal = actvGoal.getText() != null ? actvGoal.getText().toString().trim() : "";

        // Validate
        if (TextUtils.isEmpty(name)) {
            etExerciseName.setError("Vui lòng nhập tên bài tập");
            etExerciseName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(level)) {
            actvDifficultyLevel.setError("Vui lòng chọn độ khó");
            actvDifficultyLevel.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(goal)) {
            actvGoal.setError("Vui lòng chọn mục tiêu");
            actvGoal.requestFocus();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // calories per minute
        double caloriesPerMinute = 10.0; // default
        if (!TextUtils.isEmpty(caloriesStr)) {
            try {
                caloriesPerMinute = Double.parseDouble(caloriesStr);
            } catch (NumberFormatException e) {
                etCaloriesPerMinute.setError("Calo/phút phải là số");
                etCaloriesPerMinute.requestFocus();
                return;
            }
        }

        showLoading(true);

        // Build request parts
        RequestBody rbExerciseName = toRequestBody(name);
        RequestBody rbCaloriesPerMinute = toRequestBody(String.valueOf(caloriesPerMinute));
        RequestBody rbDescription = toRequestBody(desc);
        RequestBody rbInstructions = toRequestBody(instructions);
        RequestBody rbDifficultyLevel = toRequestBody(level);
        RequestBody rbCategoryId = toRequestBody(String.valueOf(selectedCategory.getCategoryId()));
        RequestBody rbGoal = toRequestBody(goal);

        // Image part
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            try {
                File imageFile = getFileFromUri(imageUri);
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageBody);
            } catch (Exception e) {
                Log.e(TAG, "Error processing image", e);
            }
        }

        // Video part
        MultipartBody.Part videoPart = null;
        if (videoUri != null) {
            try {
                File videoFile = getFileFromUri(videoUri);
                RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
                videoPart = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody);
            } catch (Exception e) {
                Log.e(TAG, "Error processing video", e);
            }
        }

        if (isEditMode && editingExercise != null) {
            // Update
            apiService.updateExercise(editingExercise.getExerciseId(),
                    rbExerciseName, rbCaloriesPerMinute, rbDescription, rbInstructions,
                    rbDifficultyLevel, rbCategoryId, rbGoal, imagePart, videoPart
            ).enqueue(exerciseCallback("Cập nhật bài tập thành công"));
        } else {
            // Create
            apiService.createExercise(
                    rbExerciseName, rbCaloriesPerMinute, rbDescription, rbInstructions,
                    rbDifficultyLevel, rbCategoryId, rbGoal, imagePart, videoPart
            ).enqueue(exerciseCallback("Thêm bài tập thành công"));
        }
    }

    /**
     * Callback xử lý response khi lưu bài tập
     */
    private Callback<ApiResponse<ExerciseResponse>> exerciseCallback(String successMsg) {
        return new Callback<ApiResponse<ExerciseResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ExerciseResponse>> call,
                                   Response<ApiResponse<ExerciseResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddExerciseActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddExerciseActivity.this, 
                            "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ExerciseResponse>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Save exercise failed", t);
                Toast.makeText(AddExerciseActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Hiển thị/ẩn loading indicator
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSaveExercise.setEnabled(!show);
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value != null ? value : "");
    }

    private File getFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("exercise_file", ".tmp", getCacheDir());
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

    // ================= IMAGE =================
    private void showImageInfo(Uri uri) {
        tvImageFileName.setText(getDisplayName(uri));
        layoutImagePreview.setVisibility(View.VISIBLE);

        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            ivImagePreview.setImageBitmap(bmp);
            if (is != null) is.close();
        } catch (Exception e) {
            ivImagePreview.setImageURI(uri);
        }
    }

    private void clearImage() {
        imageUri = null;
        tvImageFileName.setText("Chưa có ảnh nào được chọn");
        ivImagePreview.setImageDrawable(null);
        layoutImagePreview.setVisibility(View.GONE);
    }

    // ================= VIDEO =================
    private void showVideoInfo(Uri uri) {
        tvVideoFileName.setText(getDisplayName(uri));
        layoutVideoPreview.setVisibility(View.VISIBLE);

        long sizeBytes = getFileSize(uri);
        tvVideoSize.setText("Kích thước: " + formatBytes(sizeBytes));

        long durationMs = getVideoDuration(uri);
        tvVideoDuration.setText("Thời lượng: " + formatDuration(durationMs));

        Bitmap thumb = getVideoThumbnail(uri);
        if (thumb != null) ivVideoThumbnail.setImageBitmap(thumb);
        else ivVideoThumbnail.setImageDrawable(null);
    }

    private void clearVideo() {
        videoUri = null;
        tvVideoFileName.setText("Chưa có video nào được chọn");
        tvVideoDuration.setText("Thời lượng: --:--");
        tvVideoSize.setText("Kích thước: -- MB");
        ivVideoThumbnail.setImageDrawable(null);
        layoutVideoPreview.setVisibility(View.GONE);
    }

    // ================= HELPERS =================
    private String getDisplayName(Uri uri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return cursor.getString(idx);
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }
        return "Không rõ tên file";
    }

    private long getFileSize(Uri uri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (idx >= 0) return cursor.getLong(idx);
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0L;
    }

    private long getVideoDuration(Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(this, uri);
            String dur = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (dur != null) return Long.parseLong(dur);
        } catch (Exception ignored) {
        } finally {
            try { mmr.release(); } catch (Exception ignored) {}
        }
        return 0L;
    }

    private Bitmap getVideoThumbnail(Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(this, uri);
            return mmr.getFrameAtTime(0);
        } catch (Exception ignored) {
        } finally {
            try { mmr.release(); } catch (Exception ignored) {}
        }
        return null;
    }

    private String formatDuration(long ms) {
        if (ms <= 0) return "--:--";
        long totalSec = ms / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        if (min >= 60) {
            long h = min / 60;
            long m = min % 60;
            return String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, sec);
        }
        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    private String formatBytes(long bytes) {
        if (bytes <= 0) return "-- MB";
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;
        if (gb >= 1) return String.format(Locale.getDefault(), "%.2f GB", gb);
        if (mb >= 1) return String.format(Locale.getDefault(), "%.2f MB", mb);
        return String.format(Locale.getDefault(), "%.0f KB", kb);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
