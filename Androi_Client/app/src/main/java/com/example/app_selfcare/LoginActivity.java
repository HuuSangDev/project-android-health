package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_selfcare.Data.Model.Request.UserLoginRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.UserLoginResponse;
import com.example.app_selfcare.Data.Model.Response.UserProfileResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import com.example.app_selfcare.Ui.Admin.AdminHomeActivity;
import com.example.app_selfcare.upload.InforSex;
import com.example.app_selfcare.utils.FcmTokenManager;
import com.example.app_selfcare.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LoginActivity - Màn hình đăng nhập của ứng dụng
 * 
 * Chức năng chính:
 * - Cho phép người dùng đăng nhập bằng email và mật khẩu
 * - Xác thực thông tin đăng nhập qua API
 * - Phân quyền điều hướng (Admin -> AdminHome, User -> Home/InforSex)
 * - Lưu token xác thực vào SharedPreferences
 * - Đăng ký FCM token để nhận push notification
 */
public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;

    /**
     * Áp dụng ngôn ngữ đã lưu trước khi Activity được tạo
     * @param newBase Context gốc
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    /**
     * Khởi tạo Activity, thiết lập giao diện và các sự kiện click
     * @param savedInstanceState Trạng thái đã lưu của Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Bắt buộc chế độ sáng cho màn hình đăng nhập
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Xử lý padding cho system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tranglogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các view từ layout
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Sự kiện chuyển sang màn hình đăng ký
        findViewById(R.id.txtRegister).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Sự kiện chuyển sang màn hình quên mật khẩu
        findViewById(R.id.txtForgotPassword).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Sự kiện nhấn nút đăng nhập
        btnLogin.setOnClickListener(v -> {
            doLogin();
        });
    }

    /**
     * Thực hiện đăng nhập
     * - Validate input (email, password không được rỗng)
     * - Gọi API login
     * - Xử lý response: lưu token, đăng ký FCM, điều hướng theo role
     */
    private void doLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra input rỗng
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo request và gọi API
        UserLoginRequest request = new UserLoginRequest(email, password);
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.login(request).enqueue(new Callback<ApiResponse<UserLoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserLoginResponse>> call, Response<ApiResponse<UserLoginResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<UserLoginResponse> apiRes = response.body();

                    if (apiRes.getCode() == 200 && apiRes.getResult().isAuthenticated()) {

                        // Lấy token và role từ response
                        String token = apiRes.getResult().getToken();
                        String role  = apiRes.getResult().getRole();

                        // Lưu token vào SharedPreferences để sử dụng cho các request sau
                        saveToken(token);

                        // Đăng ký FCM token để nhận push notification
                        registerFcmToken();

                        // Kiểm tra role và điều hướng
                        if (role == null) {
                            Toast.makeText(LoginActivity.this, "Không tìm thấy role!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // Admin -> AdminHomeActivity
                        if ("ADMIN".equalsIgnoreCase(role)) {
                            Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                        } else {
                            // User thường -> kiểm tra profile đã tạo chưa
                            checkUserProfile(token);
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserLoginResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Lưu JWT token vào SharedPreferences
     * @param token JWT token nhận được từ server
     */
    private void saveToken(String token) {
        getSharedPreferences("APP_DATA", MODE_PRIVATE)
                .edit()
                .putString("TOKEN", token)
                .apply();
    }

    /**
     * Kiểm tra user đã tạo profile chưa
     * - Nếu chưa có profile -> chuyển đến InforSex để tạo profile
     * - Nếu đã có profile -> chuyển đến HomeActivity
     * @param token JWT token để gọi API
     */
    private void checkUserProfile(String token) {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getUserProfile("Bearer " + token).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {

                // Lỗi response -> chuyển đến tạo profile
                if (!response.isSuccessful() || response.body() == null) {
                    goToInforSex();
                    return;
                }

                UserResponse user = response.body().getResult();

                // Chưa có user hoặc profile -> chuyển đến tạo profile
                if (user == null || user.getUserProfileResponse() == null) {
                    goToInforSex();
                    return;
                }

                UserProfileResponse profile = user.getUserProfileResponse();

                // Kiểm tra profile có rỗng không (tất cả field đều null)
                boolean isProfileEmpty =
                        profile.getAvatarUrl() == null &&
                                profile.getDateOfBirth() == null &&
                                profile.getGender() == null &&
                                profile.getHeight() == null &&
                                profile.getWeight() == null &&
                                profile.getHealthGoal() == null;

                if (isProfileEmpty) {
                    // Chưa tạo profile -> màn hình tạo profile
                    goToInforSex();
                } else {
                    // Đã có profile -> màn hình chính
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                // Lỗi kết nối -> mặc định chuyển đến tạo profile
                goToInforSex();
            }
        });
    }

    /**
     * Chuyển đến màn hình tạo profile (InforSex - chọn giới tính)
     */
    private void goToInforSex() {
        Intent intent = new Intent(LoginActivity.this, InforSex.class);
        startActivity(intent);
        finish();
    }

    /**
     * Đăng ký FCM token với server để nhận push notification
     * Được gọi sau khi login thành công
     */
    private void registerFcmToken() {
        FcmTokenManager fcmTokenManager = new FcmTokenManager(this);
        fcmTokenManager.registerToken();
    }
}
