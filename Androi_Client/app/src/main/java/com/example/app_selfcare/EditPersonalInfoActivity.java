package com.example.app_selfcare;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.UserProfileResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.utils.LocaleManager;

import java.io.File;
import java.io.FileOutputStream;
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

public class EditPersonalInfoActivity extends AppCompatActivity {

    private ImageView btnBack, profileImage, btnChangeAvatar;
    private TextView tvDateOfBirth;
    private LinearLayout layoutDateOfBirth;
    private EditText etHeight, etWeight;
    private Spinner spinnerGender, spinnerHealthGoal;
    private Button btnSave;
    private ProgressBar progressBar;

    private Uri selectedImageUri;
    private String currentAvatarUrl;
    private Calendar selectedDate = Calendar.getInstance();

    private final String[] genderOptions = {"MALE", "FEMALE"};
    private final String[] genderDisplayVi = {"Nam", "Nữ"};
    private final String[] healthGoalOptions = {"WEIGHT_LOSS", "MAINTAIN", "WEIGHT_GAIN"};
    private final String[] healthGoalDisplayVi = {"Giảm cân", "Giữ cân", "Tăng cân"};

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_personal_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSpinners();
        setupImagePicker();
        setupEvents();
        loadCurrentProfile();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        profileImage = findViewById(R.id.profileImage);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        layoutDateOfBirth = findViewById(R.id.layoutDateOfBirth);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerHealthGoal = findViewById(R.id.spinnerHealthGoal);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genderDisplayVi);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, healthGoalDisplayVi);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHealthGoal.setAdapter(goalAdapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(this)
                                .load(selectedImageUri)
                                .placeholder(R.drawable.ic_proflie)
                                .into(profileImage);
                    }
                }
        );
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnChangeAvatar.setOnClickListener(v -> openImagePicker());
        profileImage.setOnClickListener(v -> openImagePicker());

        layoutDateOfBirth.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    tvDateOfBirth.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadCurrentProfile() {
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, R.string.please_login_again, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getUserProfile("Bearer " + token).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    Toast.makeText(EditPersonalInfoActivity.this, R.string.cannot_load_info, Toast.LENGTH_SHORT).show();
                    return;
                }

                UserResponse user = response.body().getResult();
                UserProfileResponse profile = user.getUserProfileResponse();

                if (profile != null) {
                    currentAvatarUrl = profile.getAvatarUrl();
                    if (currentAvatarUrl != null && !currentAvatarUrl.isEmpty()) {
                        Glide.with(EditPersonalInfoActivity.this)
                                .load(currentAvatarUrl)
                                .placeholder(R.drawable.ic_proflie)
                                .error(R.drawable.ic_proflie)
                                .into(profileImage);
                    }

                    if (profile.getDateOfBirth() != null) {
                        tvDateOfBirth.setText(profile.getDateOfBirth());
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            selectedDate.setTime(sdf.parse(profile.getDateOfBirth()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (profile.getGender() != null) {
                        for (int i = 0; i < genderOptions.length; i++) {
                            if (genderOptions[i].equalsIgnoreCase(profile.getGender())) {
                                spinnerGender.setSelection(i);
                                break;
                            }
                        }
                    }

                    if (profile.getHeight() != null) {
                        etHeight.setText(String.valueOf(profile.getHeight().intValue()));
                    }

                    if (profile.getWeight() != null) {
                        etWeight.setText(String.valueOf(profile.getWeight().intValue()));
                    }

                    if (profile.getHealthGoal() != null) {
                        for (int i = 0; i < healthGoalOptions.length; i++) {
                            if (healthGoalOptions[i].equalsIgnoreCase(profile.getHealthGoal())) {
                                spinnerHealthGoal.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditPersonalInfoActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveProfile() {
        String dateOfBirth = tvDateOfBirth.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (dateOfBirth.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, R.string.please_fill_all, Toast.LENGTH_SHORT).show();
            return;
        }

        double height, weight;
        try {
            height = Double.parseDouble(heightStr);
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = genderOptions[spinnerGender.getSelectedItemPosition()];
        String healthGoal = healthGoalOptions[spinnerHealthGoal.getSelectedItemPosition()];

        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, R.string.please_login_again, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        RequestBody genderBody = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody dateOfBirthBody = RequestBody.create(MediaType.parse("text/plain"), dateOfBirth);
        RequestBody heightBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(height));
        RequestBody weightBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(weight));
        RequestBody healthGoalBody = RequestBody.create(MediaType.parse("text/plain"), healthGoal);

        MultipartBody.Part avatarPart = null;
        if (selectedImageUri != null) {
            try {
                File file = getFileFromUri(selectedImageUri);
                if (file != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    avatarPart = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ApiService api = ApiClient.getClientWithToken(this).create(ApiService.class);
        api.updateProfileWithToken(genderBody, dateOfBirthBody, heightBody, weightBody, healthGoalBody, avatarPart)
                .enqueue(new Callback<ApiResponse<UserResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                        showLoading(false);
                        android.util.Log.d("EditProfile", "Response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            android.util.Log.d("EditProfile", "Success: " + response.body().getMessage());
                            Toast.makeText(EditPersonalInfoActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                android.util.Log.e("EditProfile", "Error: " + errorBody);
                                Toast.makeText(EditPersonalInfoActivity.this, "Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(EditPersonalInfoActivity.this, R.string.update_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                        showLoading(false);
                        android.util.Log.e("EditProfile", "Failure: " + t.getMessage(), t);
                        Toast.makeText(EditPersonalInfoActivity.this, R.string.connection_error + ": " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = new File(getCacheDir(), "temp_avatar_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }
}
