// app/src/main/java/com/example/app_selfcare/Ui/Admin/AddExerciseActivity.java
package com.example.app_selfcare;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.Data.Model.Exercise;
import com.example.app_selfcare.Data.local.ExerciseStorage;
import com.example.app_selfcare.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.util.Locale;

public class AddExerciseActivity extends AppCompatActivity {

    // ===== Inputs =====
    private TextInputEditText etExerciseName;
    private TextInputEditText etCaloriesPerMinute;
    private TextInputEditText etDescription;
    private TextInputEditText etInstructions;
    private TextInputEditText etEquipmentNeeded;
    private TextInputEditText etMuscleGroups;
    private MaterialAutoCompleteTextView actvDifficultyLevel;

    private MaterialButton btnSaveExercise;

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

        initViews();
        setupDropdown();
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
//        etExerciseName = findViewById(R.id.etExerciseName);
//        etCaloriesPerMinute = findViewById(R.id.etCaloriesPerMinute);
        etDescription = findViewById(R.id.etDescription);
        etInstructions = findViewById(R.id.etInstructions);
        etEquipmentNeeded = findViewById(R.id.etEquipmentNeeded);
        etMuscleGroups = findViewById(R.id.etMuscleGroups);
        actvDifficultyLevel = findViewById(R.id.actvDifficultyLevel);

        btnSaveExercise = findViewById(R.id.btnSaveExercise);

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

    private void setupDropdown() {
        // nếu bạn đã có array: R.array.exercise_difficulties thì giữ như cũ
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.exercise_difficulties)
        );
        actvDifficultyLevel.setAdapter(adapter);
        actvDifficultyLevel.setOnClickListener(v -> actvDifficultyLevel.showDropDown());
    }

    private void setupToolbar() {
        View back = findViewById(R.id.backButton);
        if (back != null) back.setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        // chọn ảnh/video
        btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSelectVideo.setOnClickListener(v -> pickVideoLauncher.launch("video/*"));

        // xoá ảnh/video
        btnRemoveImage.setOnClickListener(v -> clearImage());
        btnRemoveVideo.setOnClickListener(v -> clearVideo());

        // lưu
        btnSaveExercise.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        String name = safeText(etExerciseName);
        String caloriesStr = safeText(etCaloriesPerMinute);
        String desc = safeText(etDescription);
        String instructions = safeText(etInstructions);
        String equipment = safeText(etEquipmentNeeded);
        String muscle = safeText(etMuscleGroups);
        String level = actvDifficultyLevel.getText() != null ? actvDifficultyLevel.getText().toString().trim() : "";

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

        // calories per minute
        double caloriesPerMinute = 10.0; // mặc định
        if (!TextUtils.isEmpty(caloriesStr)) {
            try {
                caloriesPerMinute = Double.parseDouble(caloriesStr);
            } catch (NumberFormatException e) {
                etCaloriesPerMinute.setError("Calo/phút phải là số");
                etCaloriesPerMinute.requestFocus();
                return;
            }
        }

        // NOTE: model Exercise của bạn hiện đang dùng durationMinutes.
        // Layout mới không có "durationMinutes" riêng, nên bạn có thể:
        // 1) tự thêm field thời lượng (recommended), hoặc
        // 2) set mặc định 30 phút như bạn đang làm.
        int durationMinutes = 30;

        // Tạo object Exercise mới
        Exercise newExercise = new Exercise();
        newExercise.setName(name);

        newExercise.setDescription(TextUtils.isEmpty(desc) ? "Bài tập tốt cho sức khỏe" : desc);

        // Nếu model có field khác thì set thêm (tuỳ model của bạn)
        // newExercise.setInstructions(instructions);
        // newExercise.setEquipmentNeeded(equipment);
        // newExercise.setMuscleGroups(muscle);

        newExercise.setDurationMinutes(durationMinutes);

        int caloriesBurned = (int) Math.round(durationMinutes * caloriesPerMinute);
        newExercise.setCaloriesBurned(caloriesBurned);

        newExercise.setDifficulty(level);
        newExercise.setCategoryId("default");

        // Nếu bạn chỉ lưu local bằng drawable id:
        // -> giữ mặc định (vì imageUri/videoUri là file ngoài, model bạn chưa có field)
        newExercise.setImageResId(R.drawable.ic_active);

        // Nếu bạn muốn lưu đường dẫn uri (cần model có field String):
        // newExercise.setImageUri(imageUri != null ? imageUri.toString() : null);
        // newExercise.setVideoUri(videoUri != null ? videoUri.toString() : null);

        ExerciseStorage.addExercise(this, newExercise);

        Toast.makeText(this, "Thêm bài tập thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
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
