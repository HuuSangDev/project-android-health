package com.SelfCare.SelftCare.Entity;

import com.SelfCare.SelftCare.Enum.DifficultyLevel;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.MealType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long foodId;

    String foodName; // tên thực phẩm
    Double caloriesPer100g; // calo trên 100g
    Double proteinPer100g = 0.0; // protein trên 100g
    Double fatPer100g = 0.0; // chất béo trên 100g
    Double fiberPer100g = 0.0; // chất xơ trên 100g
    Double sugarPer100g = 0.0; // đường trên 100g


    // ---- Recipe Info ----
    String instructions; // hướng dẫn nấu ăn
    Integer prepTime; // phút chuẩn bị
    Integer cookTime; // phút nấu
    Integer servings = 1; // khẩu phần

    @Enumerated(EnumType.STRING)
    DifficultyLevel difficultyLevel;

    String imageUrl;

    @Enumerated(EnumType.STRING)
    MealType mealType = MealType.ALL; // mặc định ALL
    LocalDateTime createdAt;


    @Enumerated(EnumType.STRING)
    Goal goal;
    @ManyToOne
    @JoinColumn(name = "category_id")
    FoodCategory foodCategory;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
