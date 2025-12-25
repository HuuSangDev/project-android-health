package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.Food;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.MealType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FoodRepository extends JpaRepository<Food, Long>, JpaSpecificationExecutor<Food> {
    List<Food> findByGoalAndMealType(Goal goal, MealType mealType);
    List<Food> findByGoal(Goal goal);
    List<Food> findByGoalAndFoodCategory_CategoryId(Goal goal, Long categoryId);
    List<Food> findByFoodCategory_CategoryId(Long categoryId);
}

