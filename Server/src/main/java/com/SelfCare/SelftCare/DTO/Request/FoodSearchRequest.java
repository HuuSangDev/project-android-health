package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.DifficultyLevel;
import com.SelfCare.SelftCare.Enum.MealType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FoodSearchRequest {
    String keyword;                 // tên món ăn
    MealType mealType;              // BREAKFAST / LUNCH / DINNER
    DifficultyLevel difficultyLevel;// EASY / MEDIUM / HARD

    Double minCalories;
    Double maxCalories;
    String categoryName;
    Long categoryId;

    // 🔥 nâng cao – phân trang & sắp xếp
    @Builder.Default
    Integer page = 0;
    @Builder.Default
    Integer size = 10;
    @Builder.Default
    String sortBy = "createdAt";
    @Builder.Default
    String sortDir = "desc";
}
