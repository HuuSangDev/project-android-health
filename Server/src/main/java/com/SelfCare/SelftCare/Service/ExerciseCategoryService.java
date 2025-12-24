package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.ExerciseCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Response.ExerciseCategoryResponse;
import com.SelfCare.SelftCare.Entity.ExerciseCategory;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Repository.ExerciseCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExerciseCategoryService {

    ExerciseCategoryRepository categoryRepository;

    // Tạo category
    public ExerciseCategoryResponse createCategory(ExerciseCategoryCreateRequest request) {
        if (request.getCategoryName() == null || request.getCategoryName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        ExerciseCategory category = ExerciseCategory.builder()
                .categoryName(request.getCategoryName().trim())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .build();

        categoryRepository.save(category);

        return toResponse(category);
    }

    // Lấy tất cả categories
    public List<ExerciseCategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Cập nhật category
    public ExerciseCategoryResponse updateCategory(Long categoryId, ExerciseCategoryCreateRequest request) {
        if (request.getCategoryName() == null || request.getCategoryName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        ExerciseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_CATEGORY_NOT_FOUND));

        category.setCategoryName(request.getCategoryName().trim());
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());
        categoryRepository.save(category);

        return toResponse(category);
    }

    // Xóa category
    public void deleteCategory(Long categoryId) {
        ExerciseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_CATEGORY_NOT_FOUND));
        
        // Kiểm tra xem category có đang được sử dụng không
        // Nếu có exercises đang dùng category này, có thể throw exception hoặc soft delete
        // Hiện tại cho phép xóa trực tiếp
        categoryRepository.delete(category);
    }

    private ExerciseCategoryResponse toResponse(ExerciseCategory category) {
        int exerciseCount = category.getExercises() != null ? category.getExercises().size() : 0;
        return ExerciseCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .exerciseCount(exerciseCount)
                .build();
    }
}
