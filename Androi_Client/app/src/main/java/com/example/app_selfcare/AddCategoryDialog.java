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
            newCategory.setName(name);
            newCategory.setType("food"); // hoặc "exercise" tùy bạn
            newCategory.setIconResId(R.drawable.ic_category_food);

            if (listener != null) listener.onCategoryAdded(newCategory);
            Toast.makeText(getContext(), "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        return dialog;
    }
}