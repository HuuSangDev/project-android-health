//package com.example.app_selfcare;
//
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import com.google.android.material.textfield.TextInputEditText; // Sử dụng TextInputEditText thay vì EditText
//
//public class AddCategoryActivity extends AppCompatActivity {
//
//    private Toolbar toolbar;
//    private TextInputEditText etCategoryName;
//    private TextInputEditText etCategoryDescription;
//    private Button btnSaveCategory;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // Lưu ý: Đảm bảo tên layout là activity_add_category
//        setContentView(R.layout.activity_add_category);
//
//        initViews();
//        setupEvents();
//    }
//
//    private void initViews() {
//        // Thiết lập Toolbar
//        toolbar = findViewById(R.id.toolbarCategory);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("Thêm Danh Mục Thực Phẩm");
//        }
//
//        // Ánh xạ các trường nhập liệu
//        etCategoryName = findViewById(R.id.etCategoryName);
//        etCategoryDescription = findViewById(R.id.etCategoryDescription);
//        btnSaveCategory = findViewById(R.id.btnSaveCategory);
//    }
//
//    private void setupEvents() {
//        btnSaveCategory.setOnClickListener(v -> {
//            String name = etCategoryName.getText().toString().trim();
//            String description = etCategoryDescription.getText().toString().trim();
//
//            if (name.isEmpty()) {
//                Toast.makeText(this, "Vui lòng nhập Tên Danh mục", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // TODO: Gửi yêu cầu POST tới API (dùng Retrofit/Volley...)
//            // DTO Backend: FoodCategoryCreateRequest(name, description)
//
//            Toast.makeText(this, "Đã tạo Danh mục: " + name, Toast.LENGTH_LONG).show();
//            // finish(); // Quay lại sau khi lưu thành công
//        });
//    }
//
//    /**
//     * Xử lý sự kiện nhấn nút Back/Home trên Toolbar
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}