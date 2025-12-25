package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.FoodCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Response.FoodCategoryResponse;
import com.SelfCare.SelftCare.Entity.FoodCategory;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Repository.FoodCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodCategoryService {

    FoodCategoryRepository categoryRepository;

    // Tạo category
    public FoodCategoryResponse createCategory(FoodCategoryCreateRequest request) {
        FoodCategory category = FoodCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .build();
        categoryRepository.save(category);

        return toResponse(category);
    }

    // Lấy tất cả categories
    public List<FoodCategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Cập nhật category
    public FoodCategoryResponse updateCategory(Long categoryId, FoodCategoryCreateRequest request) {
        FoodCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_CATEGORY_NOT_FOUND));

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);

        return toResponse(category);
    }

    // Xóa category
    public void deleteCategory(Long categoryId) {
        FoodCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }

    private FoodCategoryResponse toResponse(FoodCategory category) {
        int foodCount = category.getFoods() != null ? category.getFoods().size() : 0;
        return FoodCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .foodCount(foodCount)
                .build();
    }
}
