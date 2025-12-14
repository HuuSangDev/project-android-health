package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.CategoryAdapter;
import com.example.app_selfcare.AddCategoryDialog;
import com.example.app_selfcare.Data.Model.Category;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class FoodCategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private CategoryAdapter adapter;
    private List<Category> categoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewFoodCategories);
        fabAdd = view.findViewById(R.id.fabAddCategory);

        categoryList = new ArrayList<>();
        categoryList.add(new Category(10, "Món chính", "food", R.drawable.ic_food));
        categoryList.add(new Category(11, "Đồ uống", "food", R.drawable.ic_food));
        categoryList.add(new Category(12, "Tráng miệng", "food", R.drawable.ic_food));

        setupRecyclerView();
        setupFabButton();
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onEdit(Category category) {
                Toast.makeText(getContext(), "Sửa: " + category.name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(int id) {
                for (Category c : categoryList) {
                    if (c.id == id) {
                        categoryList.remove(c);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            AddCategoryDialog dialog = new AddCategoryDialog();
            dialog.setListener(category -> {
                category.type = "food"; // QUAN TRỌNG
                categoryList.add(category);
                adapter.notifyItemInserted(categoryList.size() - 1);
            });
            dialog.show(getParentFragmentManager(), "add_food");
        });
    }
}
