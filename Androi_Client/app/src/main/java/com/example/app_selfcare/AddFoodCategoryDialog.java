package com.example.app_selfcare;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.app_selfcare.Data.Model.Request.FoodCategoryCreateRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.FoodCategoryResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFoodCategoryDialog extends DialogFragment {

    private static final String TAG = "AddFoodCategoryDialog";

    public interface OnCategoryAddedListener {
        void onCategoryAdded(FoodCategoryResponse category);
    }

    private OnCategoryAddedListener listener;
    private ProgressBar progressBar;

    public void setListener(OnCategoryAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_food_category, null);

        TextInputEditText etName = view.findViewById(R.id.etCategoryName);
        TextInputEditText etDescription = view.findViewById(R.id.etCategoryDescription);
        TextInputEditText etIconUrl = view.findViewById(R.id.etCategoryIconUrl);
        progressBar = view.findViewById(R.id.progressBar);

        // Tạo dialog không có nút mặc định
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(true)
                .create();

        // Xử lý sự kiện cho các nút trong layout
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnSaveCategory).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
            String iconUrl = etIconUrl.getText() != null ? etIconUrl.getText().toString().trim() : "";

            if (TextUtils.isEmpty(name)) {
                etName.setError("Nhập tên danh mục");
                return;
            }

            createCategory(name, description, iconUrl);
        });

        return dialog;
    }

    private void createCategory(String name, String description, String iconUrl) {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);
        FoodCategoryCreateRequest request = new FoodCategoryCreateRequest(name, description, iconUrl);

        apiService.createFoodCategory(request).enqueue(new Callback<ApiResponse<FoodCategoryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FoodCategoryResponse>> call,
                                   Response<ApiResponse<FoodCategoryResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    FoodCategoryResponse category = response.body().getResult();
                    if (listener != null) {
                        listener.onCategoryAdded(category);
                    }
                    Toast.makeText(getContext(), "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FoodCategoryResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Create category failed", t);
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
