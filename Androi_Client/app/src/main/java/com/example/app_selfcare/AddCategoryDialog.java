// File: app/src/main/java/com/example/app_selfcare/AddCategoryDialog.java
package com.example.app_selfcare;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.app_selfcare.Data.Model.Category;
import com.example.app_selfcare.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddCategoryDialog extends DialogFragment {
    private String type;

    public void setType(String type) {
        this.type = type;
    }
    public interface OnCategoryAddedListener {
        void onCategoryAdded(Category category);
    }

    private OnCategoryAddedListener listener;

    public void setListener(OnCategoryAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        TextInputEditText etName = view.findViewById(R.id.etCategoryName);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnSaveCategory).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                etName.setError("Vui lòng nhập tên danh mục");
                return;
            }

            Category newCategory = new Category();
            newCategory.name = name;
            newCategory.type = "food";
            newCategory.iconResId = R.drawable.ic_category_food; // nếu chưa có thì dùng tạm:
            // newCategory.iconResId = android.R.drawable.ic_menu_gallery;

            // Sửa lỗi cú pháp: System.currentTimeMillis() không có hashCode()
            newCategory.id = (int) System.currentTimeMillis(); // hoặc để 0 cũng được

            if (listener != null) {
                listener.onCategoryAdded(newCategory);
            }

            Toast.makeText(getContext(), "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        return dialog;
    }
}