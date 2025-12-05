package com.SelfCare.SelftCare.DTO.Request;

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
public class CreateExerciseRequest {

     String exerciseName;
     Double caloriesPerMinute;
     String description;
     String instructions;
     String difficultyLevel;
     String equipmentNeeded;
     String muscleGroups;

     Long categoryId;

     MultipartFile image;
     MultipartFile video;
}

