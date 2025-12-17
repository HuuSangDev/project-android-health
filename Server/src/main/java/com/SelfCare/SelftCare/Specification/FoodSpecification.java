package com.SelfCare.SelftCare.Specification;

import com.SelfCare.SelftCare.Entity.Food;
import com.SelfCare.SelftCare.Enum.DifficultyLevel;
import com.SelfCare.SelftCare.Enum.MealType;
import org.springframework.data.jpa.domain.Specification;

public class FoodSpecification {
    public static Specification<Food> nameContains(String keyword) {
        return (root, query, cb) ->
                keyword == null || keyword.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("foodName")),
                        "%" + keyword.toLowerCase() + "%"
                );
    }

    public static Specification<Food> hasMealType(MealType mealType) {
        return (root, query, cb) ->
                mealType == null || mealType == MealType.ALL
                        ? null
                        : cb.equal(root.get("mealType"), mealType);
    }

    public static Specification<Food> hasDifficulty(DifficultyLevel level) {
        return (root, query, cb) ->
                level == null ? null : cb.equal(root.get("difficultyLevel"), level);
    }

    public static Specification<Food> caloriesBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("caloriesPer100g"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("caloriesPer100g"), min);
            return cb.between(root.get("caloriesPer100g"), min, max);
        };
    }

    public static Specification<Food> hasCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null
                        ? null
                        : cb.equal(root.get("foodCategory").get("id"), categoryId);
    }


    public static Specification<Food> hasCategoryName(String categoryName) {
        return (root, query, cb) ->
                categoryName == null || categoryName.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("foodCategory").get("categoryName")),
                        "%" + categoryName.toLowerCase() + "%"
                );
    }
}
