package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.FoodCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Response.FoodCategoryResponse;
import com.SelfCare.SelftCare.Service.FoodCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodCategoryController {

    FoodCategoryService foodCategoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<FoodCategoryResponse> createCategory(@RequestBody FoodCategoryCreateRequest request) {
        return ApiResponse.<FoodCategoryResponse>builder()
                .message("create food category success")
                .result(foodCategoryService.createCategory(request))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<FoodCategoryResponse>> getAllCategories() {
        return ApiResponse.<List<FoodCategoryResponse>>builder()
                .message("get all categories success")
                .result(foodCategoryService.getAllCategories())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    public ApiResponse<FoodCategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody FoodCategoryCreateRequest request) {
        return ApiResponse.<FoodCategoryResponse>builder()
                .message("update category success")
                .result(foodCategoryService.updateCategory(categoryId, request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
        foodCategoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .message("delete category success")
                .build();
    }
}
