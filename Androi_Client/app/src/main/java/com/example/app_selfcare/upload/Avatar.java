package com.example.app_selfcare.upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.HomeActivity;
import com.example.app_selfcare.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Avatar extends AppCompatActivity {

    private ImageView avatar1, avatar2, avatar3;
    private Button uploadButton, continueButton;
    private Uri selectedAvatarUri;
    private int selectedAvatarResId = -1; // -1 means no predefined avatar selected
    private String gender, healthGoal;
    private int age;
    private double height, weight;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_avatar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from previous activities
        Intent intent = getIntent();
        gender = intent.getStringExtra("user_gender");
        age = intent.getIntExtra("user_age", 19);
        height = intent.getIntExtra("user_height_cm", 170);
        weight = intent.getIntExtra("user_weight_kg", 62);
        healthGoal = intent.getStringExtra("user_health_goal");
        
        // Debug log to check received data
        android.util.Log.d("Avatar", "=== Received Data ===");
        android.util.Log.d("Avatar", "Gender: " + gender);
        android.util.Log.d("Avatar", "Age: " + age);
        android.util.Log.d("Avatar", "Height: " + height);
        android.util.Log.d("Avatar", "Weight: " + weight);
        android.util.Log.d("Avatar", "HealthGoal: " + healthGoal);

        // Initialize views
        avatar1 = findViewById(R.id.avatar1);
        avatar2 = findViewById(R.id.avatar2);
        avatar3 = findViewById(R.id.avatar3);
        uploadButton = findViewById(R.id.uploadButton);
        continueButton = findViewById(R.id.continueButton);

        // Setup image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedAvatarUri = result.getData().getData();
                        selectedAvatarResId = -1; // Clear predefined selection
                        Toast.makeText(this, "Avatar đã được chọn", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Avatar selection listeners
        avatar1.setOnClickListener(v -> {
            selectedAvatarResId = R.drawable.avatar1;
            selectedAvatarUri = null;
            updateAvatarSelection();
        });

        avatar2.setOnClickListener(v -> {
            selectedAvatarResId = R.drawable.avatar2;
            selectedAvatarUri = null;
            updateAvatarSelection();
        });

        avatar3.setOnClickListener(v -> {
            selectedAvatarResId = R.drawable.avatar3;
            selectedAvatarUri = null;
            updateAvatarSelection();
        });

        // Upload button listener
        uploadButton.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            imagePickerLauncher.launch(pickImage);
        });

        // Continue button listener
        continueButton.setOnClickListener(v -> updateUserProfile());

        // Back button listener
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void updateAvatarSelection() {
        // Reset all backgrounds
        avatar1.setBackgroundResource(R.drawable.avatar_background_gray);
        avatar2.setBackgroundResource(R.drawable.avatar_background_gray);
        avatar3.setBackgroundResource(R.drawable.avatar_background_gray);

        // Highlight selected
        if (selectedAvatarResId == R.drawable.avatar1) {
            avatar1.setBackgroundResource(R.drawable.avatar_background_orange);
        } else if (selectedAvatarResId == R.drawable.avatar2) {
            avatar2.setBackgroundResource(R.drawable.avatar_background_orange);
        } else if (selectedAvatarResId == R.drawable.avatar3) {
            avatar3.setBackgroundResource(R.drawable.avatar_background_orange);
        }
    }

    private void updateUserProfile() {
        // Debug: Log all received data
        android.util.Log.d("Avatar", "Gender: " + gender);
        android.util.Log.d("Avatar", "Age: " + age);
        android.util.Log.d("Avatar", "Height: " + height);
        android.util.Log.d("Avatar", "Weight: " + weight);
        android.util.Log.d("Avatar", "HealthGoal: " + healthGoal);
        
        // Validate data with more specific error messages
        if (gender == null) {
            Toast.makeText(this, "Thiếu thông tin giới tính. Vui lòng quay lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (healthGoal == null || healthGoal.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin mục tiêu. Vui lòng quay lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate date of birth from age (assuming birthday is today)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dateOfBirthStr = dateFormat.format(calendar.getTime());

        // Prepare multipart request
        RequestBody genderBody = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody dateOfBirthBody = RequestBody.create(MediaType.parse("text/plain"), dateOfBirthStr);
        RequestBody heightBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(height));
        RequestBody weightBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(weight));
        RequestBody healthGoalBody = RequestBody.create(MediaType.parse("text/plain"), healthGoal);

        MultipartBody.Part avatarPart = null;

        // Handle avatar - either from file or predefined
        if (selectedAvatarUri != null) {
            // Upload from file
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedAvatarUri);
                File tempFile = File.createTempFile("avatar", ".jpg", getCacheDir());
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
                avatarPart = MultipartBody.Part.createFormData("avatar", tempFile.getName(), requestFile);
            } catch (IOException e) {
                Toast.makeText(this, "Lỗi khi đọc file ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (selectedAvatarResId != -1) {
            // For predefined avatars, we'll skip avatar upload for now
            // You can convert drawable to file if needed
            Toast.makeText(this, "Avatar mặc định sẽ được sử dụng", Toast.LENGTH_SHORT).show();
        }

        // Show loading
        continueButton.setEnabled(false);
        continueButton.setText("Đang cập nhật...");

        // Call API
        ApiService apiService = ApiClient.getClientWithToken(this).create(ApiService.class);
        Call<ApiResponse<UserResponse>> call = apiService.updateProfileWithToken(
                genderBody,
                dateOfBirthBody,
                heightBody,
                weightBody,
                healthGoalBody,
                avatarPart
        );

        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                continueButton.setEnabled(true);
                continueButton.setText("Continue →");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> apiResponse = response.body();
                    if (apiResponse.getCode() == 200) {
                        Toast.makeText(Avatar.this, "Cập nhật profile thành công!", Toast.LENGTH_SHORT).show();
                        // Navigate to HomeActivity
                        Intent intent = new Intent(Avatar.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(Avatar.this, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Avatar.this, "Lỗi kết nối: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                continueButton.setEnabled(true);
                continueButton.setText("Continue →");
                
                String errorMessage = "Lỗi kết nối";
                if (t instanceof java.net.SocketException) {
                    errorMessage = "Không thể kết nối đến server. Vui lòng kiểm tra:\n" +
                            "1. Server có đang chạy không?\n" +
                            "2. IP và port có đúng không?";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Không tìm thấy server. Vui lòng kiểm tra IP: " + "http://10.0.2.2:8080";
                } else if (t instanceof java.net.ConnectException) {
                    errorMessage = "Không thể kết nối. Vui lòng đảm bảo server đang chạy trên port 8080";
                } else {
                    errorMessage = "Lỗi: " + t.getMessage();
                }
                
                android.util.Log.e("Avatar", "API Error: ", t);
                Toast.makeText(Avatar.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}