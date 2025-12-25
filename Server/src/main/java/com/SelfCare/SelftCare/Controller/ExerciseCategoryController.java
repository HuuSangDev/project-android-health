package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.ExerciseCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Response.ExerciseCategoryResponse;
import com.SelfCare.SelftCare.Service.ExerciseCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercise-categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseCategoryController {

    ExerciseCategoryService exerciseCategoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<ExerciseCategoryResponse> createCategory(
            @RequestBody ExerciseCategoryCreateRequest request) {
        return ApiResponse.<ExerciseCategoryResponse>builder()
                .result(exerciseCategoryService.createCategory(request))
                .message("Tạo danh mục bài tập thành công")
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<ExerciseCategoryResponse>> getAllCategories() {
        return ApiResponse.<List<ExerciseCategoryResponse>>builder()
                .result(exerciseCategoryService.getAllCategories())
                .message("Lấy danh sách danh mục bài tập thành công")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    public ApiResponse<ExerciseCategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody ExerciseCategoryCreateRequest request) {
        return ApiResponse.<ExerciseCategoryResponse>builder()
                .result(exerciseCategoryService.updateCategory(categoryId, request))
                .message("Cập nhật danh mục bài tập thành công")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
        exerciseCategoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .message("Xóa danh mục bài tập thành công")
                .build();
    }
}
