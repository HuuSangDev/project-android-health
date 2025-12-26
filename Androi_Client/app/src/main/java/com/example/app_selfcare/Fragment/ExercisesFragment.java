package com.example.app_selfcare.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.AdminExerciseAdapter;
import com.example.app_selfcare.AddExerciseActivity;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ExercisesFragment - Fragment quản lý bài tập (Admin)
 * 
 * Chức năng:
 * - Hiển thị danh sách bài tập
 * - Thêm bài tập mới
 * - Sửa bài tập
 * - Xóa bài tập
 */
public class ExercisesFragment extends Fragment {

    private static final String TAG = "ExercisesFragment";
    private static final int REQUEST_ADD_EXERCISE = 1001;
    private static final int REQUEST_EDIT_EXERCISE = 1002;

    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton fabAdd;
    private View progressBar;
    private View layoutEmpty;
    
    private AdminExerciseAdapter adapter;
    private final List<ExerciseResponse> exerciseList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerView();
        setupFabButton();
        loadExercises();
    }

    /**
     * Ánh xạ các view
     */
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewExercises);
        fabAdd = view.findViewById(R.id.fabAddExercise);
        progressBar = view.findViewById(R.id.layoutLoading);
        layoutEmpty = view.findViewById(R.id.layoutEmptyState);
    }

    /**
     * Thiết lập RecyclerView với adapter
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminExerciseAdapter(exerciseList, new AdminExerciseAdapter.OnExerciseActionListener() {
            @Override
            public void onEdit(ExerciseResponse exercise) {
                openEditExercise(exercise);
            }

            @Override
            public void onDelete(ExerciseResponse exercise) {
                showDeleteConfirmDialog(exercise);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * Thiết lập nút thêm bài tập
     */
    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExerciseActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EXERCISE);
        });
    }

    /**
     * Load danh sách bài tập từ API
     */
    private void loadExercises() {
        showLoading(true);
        
        apiService.getExercises().enqueue(new Callback<ApiResponse<List<ExerciseResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ExerciseResponse>>> call,
                                   Response<ApiResponse<List<ExerciseResponse>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    exerciseList.clear();
                    exerciseList.addAll(response.body().getResult());
                    adapter.notifyDataSetChanged();
                    
                    updateEmptyState();
                    Log.d(TAG, "Loaded " + exerciseList.size() + " exercises");
                } else {
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ExerciseResponse>>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Load exercises failed", t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Mở màn hình sửa bài tập
     */
    private void openEditExercise(ExerciseResponse exercise) {
        Intent intent = new Intent(getActivity(), AddExerciseActivity.class);
        intent.putExtra("exercise", exercise);
        startActivityForResult(intent, REQUEST_EDIT_EXERCISE);
    }

    /**
     * Hiển thị dialog xác nhận xóa
     */
    private void showDeleteConfirmDialog(ExerciseResponse exercise) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa bài tập")
                .setMessage("Bạn có chắc muốn xóa bài tập \"" + exercise.getExerciseName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteExercise(exercise))
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Xóa bài tập qua API
     */
    private void deleteExercise(ExerciseResponse exercise) {
        showLoading(true);
        
        apiService.deleteExercise(exercise.getExerciseId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);
                
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa bài tập", Toast.LENGTH_SHORT).show();
                    loadExercises(); // Reload list
                } else {
                    Toast.makeText(getContext(), "Lỗi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Delete exercise failed", t);
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hiển thị/ẩn loading
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Cập nhật trạng thái empty state
     */
    private void updateEmptyState() {
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(exerciseList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_ADD_EXERCISE || requestCode == REQUEST_EDIT_EXERCISE) {
                // Reload list after add/edit
                loadExercises();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExercises();
    }
}
