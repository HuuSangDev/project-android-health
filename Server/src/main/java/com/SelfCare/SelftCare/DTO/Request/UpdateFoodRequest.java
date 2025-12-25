package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.DifficultyLevel;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.MealType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateFoodRequest {

    String foodName;

    Double caloriesPer100g;
    Double proteinPer100g;
    Double fatPer100g;
    Double fiberPer100g;
    Double sugarPer100g;

    MultipartFile image;

    String instructions;
    Integer prepTime;
    Integer cookTime;
    Integer servings;

    MealType mealType;
    DifficultyLevel difficultyLevel;

    Long categoryId;

    Goal goal;
}
