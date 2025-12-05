package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.FoodCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Response.FoodCategoryResponse;
import com.SelfCare.SelftCare.Service.FoodCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodCategoryController {

    FoodCategoryService foodCategoryService;

    @PostMapping("/create")
    public ApiResponse<FoodCategoryResponse> createCategory(@RequestBody FoodCategoryCreateRequest request) {
        return ApiResponse.<FoodCategoryResponse>builder()
                .message("create food category success")
                .result(foodCategoryService.createCategory(request))
                .build();
    }

}
