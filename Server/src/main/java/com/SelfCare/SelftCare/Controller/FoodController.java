package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.CreateFoodRequest;
import com.SelfCare.SelftCare.DTO.Request.FoodSearchRequest;
import com.SelfCare.SelftCare.DTO.Response.FoodCreateResponse;
import com.SelfCare.SelftCare.Enum.MealType;
import com.SelfCare.SelftCare.Service.FoodService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodController {

    FoodService foodService;

    @GetMapping("/{foodId}")
    ApiResponse<FoodCreateResponse> getFoodDetail(@PathVariable Long foodId) {
        return ApiResponse.<FoodCreateResponse>builder()
                .result(foodService.getFoodDetail(foodId))
                .message("Chi tiết món ăn")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    ApiResponse<FoodCreateResponse> createFood(@ModelAttribute  CreateFoodRequest request) throws IOException {
        return ApiResponse.<FoodCreateResponse>builder()
                .result(foodService.createFood(request))
                .message("Tạo món ăn thành công")
                .build();
    }


    @GetMapping("/meal/{mealType}")
    ApiResponse<List<FoodCreateResponse>> getFoodsByMealType(@PathVariable MealType mealType)  {
        return ApiResponse.<List<FoodCreateResponse>>builder()
                .result(foodService.getFoodsByMealType(mealType))
                .message("get meal success")

                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<FoodCreateResponse>> getAllFoods()  {
        return ApiResponse.<List<FoodCreateResponse>>builder()
                .message("get all foods success")
                .result(foodService.getAllFoods())
                .build();
    }


    @PostMapping("/search")
    public ApiResponse<Page<FoodCreateResponse>> searchFood(
            @RequestBody FoodSearchRequest request
    ) {
        return ApiResponse.<Page<FoodCreateResponse>>builder()
                .result(foodService.searchFood(request))
                .build();
    }




}

