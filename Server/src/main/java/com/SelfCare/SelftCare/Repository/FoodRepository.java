package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.Food;
import com.SelfCare.SelftCare.Enum.MealType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FoodRepository extends JpaRepository<Food, Long>, JpaSpecificationExecutor<Food> {
    List<Food> findByMealType(MealType mealType);


    

}

