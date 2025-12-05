package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.ExerciseCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Response.ExerciseCategoryResponse;
import com.SelfCare.SelftCare.Service.ExerciseCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercise-categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseCategoryController {

    ExerciseCategoryService exerciseCategoryService;

    @PostMapping("/create")
    public ApiResponse<ExerciseCategoryResponse> createCategory(
            @RequestBody ExerciseCategoryCreateRequest request
    ) {
        return ApiResponse.<ExerciseCategoryResponse>builder()
                .result(exerciseCategoryService.createCategory(request))
                .message("Tạo danh mục bài tập thành công")
                .build();
    }
}
