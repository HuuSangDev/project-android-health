package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.Goal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateExerciseRequest {
    @NotBlank(message = "exerciseName is required")
    String exerciseName;

    @NotNull(message = "caloriesPerMinute is required")
    @Positive(message = "caloriesPerMinute must be > 0")
    Double caloriesPerMinute;

    @NotBlank(message = "description is required")
    String description;

    @NotBlank(message = "instructions is required")
    String instructions;

    @NotBlank(message = "difficultyLevel is required")
    String difficultyLevel;

    String equipmentNeeded;
    String muscleGroups;

    // file optional
    MultipartFile image;
    MultipartFile video;

    @NotNull(message = "categoryId is required")
    Long categoryId;

    @NotNull(message = "goal is required")
    Goal goal;
}
