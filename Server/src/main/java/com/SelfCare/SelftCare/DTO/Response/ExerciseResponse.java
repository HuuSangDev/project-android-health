package com.SelfCare.SelftCare.DTO.Response;

import com.SelfCare.SelftCare.Enum.Goal;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseResponse {
     Long exerciseId;
     String exerciseName;
     Double caloriesPerMinute;
     String description;
     String instructions;
     String difficultyLevel;
     String equipmentNeeded;
     String muscleGroups;
     String imageUrl;
     String videoUrl;
     LocalDateTime createdAt;

     ExerciseCategoryResponse category;

     Goal goal;
}
