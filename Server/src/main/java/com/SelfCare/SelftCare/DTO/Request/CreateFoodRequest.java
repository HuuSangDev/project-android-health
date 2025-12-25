package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.DifficultyLevel;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFoodRequest {

    @NotBlank(message = "foodName is required")
    String foodName;

    @NotNull(message = "caloriesPer100g is required")
    Double caloriesPer100g;
    Double proteinPer100g;
    Double fiberPer100g;
    Double sugarPer100g;
    MultipartFile image;
    Double fatPer100g;

    String instructions;
    Integer prepTime;
    Integer cookTime;
    Integer servings;

    MealType mealType;


    DifficultyLevel difficultyLevel;

    Long categoryId;

    @NotNull(message = "goal is required")
    Goal goal;

}

