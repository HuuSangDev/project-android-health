package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.SaveFoodRequest;
import com.SelfCare.SelftCare.DTO.Response.SavedFoodResponse;
import com.SelfCare.SelftCare.Service.SavedFoodService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saved-foods")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SavedFoodController {

    SavedFoodService savedFoodService;

    @PostMapping("/save")
    public ApiResponse<String> saveFood(@Valid @RequestBody SaveFoodRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return ApiResponse.<String>builder()
                .result(savedFoodService.saveFood(email, request))
                .message("Thao tác thành công")
                .build();
    }

    @DeleteMapping("/unsave/{foodId}")
    public ApiResponse<String> unsaveFood(@PathVariable Long foodId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return ApiResponse.<String>builder()
                .result(savedFoodService.unsaveFood(email, foodId))
                .message("Thao tác thành công")
                .build();
    }

    @GetMapping("/my-saved-foods")
    public ApiResponse<List<SavedFoodResponse>> getMySavedFoods() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return ApiResponse.<List<SavedFoodResponse>>builder()
                .result(savedFoodService.getSavedFoods(email))
                .message("Danh sách món ăn đã lưu")
                .build();
    }

    @GetMapping("/check/{foodId}")
    public ApiResponse<Boolean> checkIfFoodSaved(@PathVariable Long foodId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return ApiResponse.<Boolean>builder()
                .result(savedFoodService.isFoodSaved(email, foodId))
                .message("Trạng thái lưu món ăn")
                .build();
    }
}