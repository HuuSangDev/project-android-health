package com.SelfCare.SelftCare.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SavedFoodResponse {

    Long savedFoodId;
    Long foodId;
    String foodName;
    String imageUrl;
    Double caloriesPer100g;
    Integer prepTime;
    Integer cookTime;
    String mealType;
    String difficultyLevel;
    LocalDateTime savedAt;
}