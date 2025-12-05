package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.CreateExerciseRequest;
import com.SelfCare.SelftCare.DTO.Response.ExerciseResponse;
import com.SelfCare.SelftCare.Entity.Exercise;
import com.SelfCare.SelftCare.Entity.ExerciseCategory;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Mapper.ExerciseMapper;
import com.SelfCare.SelftCare.Repository.ExerciseCategoryRepository;
import com.SelfCare.SelftCare.Repository.ExerciseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {

    ExerciseRepository exerciseRepository;
    ExerciseCategoryRepository categoryRepository;
    FileUploadsService fileUploadsService;
    ExerciseMapper exerciseMapper;

    public ExerciseResponse createExercise(CreateExerciseRequest request) throws IOException {

        // 1. Lấy category
        ExerciseCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_CATEGORY_NOT_FOUND));
        }

        // 2. Upload image
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = fileUploadsService.uploadImage(request.getImage());
        }

        // 3. Upload video
        String videoUrl = null;
        if (request.getVideo() != null && !request.getVideo().isEmpty()) {
            videoUrl = fileUploadsService.uploadVideo(request.getVideo());
        }

        // 4. Map tạo entity
        Exercise exercise = Exercise.builder()
                .exerciseName(request.getExerciseName())
                .caloriesPerMinute(request.getCaloriesPerMinute())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .difficultyLevel(request.getDifficultyLevel())
                .equipmentNeeded(request.getEquipmentNeeded())
                .muscleGroups(request.getMuscleGroups())
                .imageUrl(imageUrl)
                .videoUrl(videoUrl)
                .exerciseCategory(category)
                .build();

        Exercise saved = exerciseRepository.save(exercise);

        // 5. Dùng mapper để trả response
        return exerciseMapper.toResponse(saved);
    }

    public List<ExerciseResponse> getAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(exerciseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ExerciseResponse> getExercisesByCategory(Long categoryId) {
        return exerciseRepository.findByExerciseCategory_CategoryId(categoryId)
                .stream()
                .map(exerciseMapper::toResponse)
                .collect(Collectors.toList());
    }
}
