package com.SelfCare.SelftCare.DTO.Response;

import com.SelfCare.SelftCare.Enum.DifficultyLevel;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.MealType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodCreateResponse {
    Long foodId;
    String foodName;
    Double caloriesPer100g;
    Double proteinPer100g;
    Double fatPer100g;
    Double fiberPer100g;
    Double sugarPer100g;
    String imageUrl; // <--- sửa từ MultipartFile sang String
    LocalDateTime createdAt;



    String instructions;
    Integer prepTime;
    Integer cookTime;
    Integer servings;
    DifficultyLevel difficultyLevel;

    MealType mealType;


    FoodCategoryResponse categoryResponse;
    Goal goal;
}

