package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.ExerciseCategoryCreateRequest;
import com.SelfCare.SelftCare.DTO.Response.ExerciseCategoryResponse;
import com.SelfCare.SelftCare.Entity.ExerciseCategory;
import com.SelfCare.SelftCare.Repository.ExerciseCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class ExerciseCategoryService {

    ExerciseCategoryRepository categoryRepository;

    public ExerciseCategoryResponse createCategory(ExerciseCategoryCreateRequest request) {

        ExerciseCategory category = ExerciseCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .build();

        categoryRepository.save(category);

        return ExerciseCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .build();
    }
}
