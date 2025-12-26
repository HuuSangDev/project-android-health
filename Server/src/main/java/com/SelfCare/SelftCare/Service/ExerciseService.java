package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.CreateExerciseRequest;
import com.SelfCare.SelftCare.DTO.Request.UpdateExerciseRequest;
import com.SelfCare.SelftCare.DTO.Response.ExerciseResponse;
import com.SelfCare.SelftCare.Entity.Exercise;
import com.SelfCare.SelftCare.Entity.ExerciseCategory;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Entity.UserProfile;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Mapper.ExerciseMapper;
import com.SelfCare.SelftCare.Repository.ExerciseCategoryRepository;
import com.SelfCare.SelftCare.Repository.ExerciseRepository;
import com.SelfCare.SelftCare.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    UserRepository userRepository;
    NotificationService notificationService;
    @Caching(evict = {
            @CacheEvict(value = "exercisesByGoal", allEntries = true),
            @CacheEvict(value = "exercisesByGoalAndCategory", allEntries = true),
            @CacheEvict(value = "exercise_detail", allEntries = true)
    })
    @Transactional
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


        Exercise exercise = Exercise.builder()
                .exerciseName(request.getExerciseName())
                .caloriesPerMinute(request.getCaloriesPerMinute())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .difficultyLevel(request.getDifficultyLevel())
                .imageUrl(imageUrl)
                .videoUrl(videoUrl)
                .exerciseCategory(category)
                .goal(request.getGoal())
                .build();

        Exercise saved = exerciseRepository.save(exercise);

        // Gửi thông báo qua WebSocket theo goal
        notificationService.notifyNewExercise(saved.getExerciseId(), saved.getExerciseName(), saved.getGoal());

        // 5. Dùng mapper để trả response
        return exerciseMapper.toResponse(saved);
    }

    /**
     * Lấy chi tiết bài tập theo ID
     * Tạm bỏ cache để tránh lỗi deserialize từ Redis
     */
    // @Cacheable(value = "exercise_detail", key = "#id")
    public ExerciseResponse getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));
        return exerciseMapper.toResponse(exercise);
    }

    @Cacheable(
            value = "exercisesByGoal",
            key = "#email + '_' + #isAdmin"
    )
    public List<ExerciseResponse> getAllExercisesByUserGoal(String email, boolean isAdmin) {

        // Nếu là admin, trả về tất cả exercises
        if (isAdmin) {
            return exerciseRepository.findAll()
                    .stream()
                    .map(exerciseMapper::toResponse)
                    .toList();
        }

        // Nếu là user thường, lọc theo goal
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserProfile profile = user.getUserProfile();
        if (profile == null || profile.getHealthGoal() == null) {
            throw new AppException(ErrorCode.USER_PROFILE_NULL);
        }

        Goal goal = profile.getHealthGoal();

        return exerciseRepository.findByGoal(goal)
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }

    @Cacheable(
            value = "exercisesByGoalAndCategory",
            key = "#email + '_' + #categoryId + '_' + #isAdmin"
    )
    public List<ExerciseResponse> getExercisesByCategory(
            String email,
            Long categoryId,
            boolean isAdmin
    ) {

        // Nếu là admin, trả về tất cả exercises theo category
        if (isAdmin) {
            return exerciseRepository
                    .findByExerciseCategory_CategoryId(categoryId)
                    .stream()
                    .map(exerciseMapper::toResponse)
                    .toList();
        }

        // Nếu là user thường, lọc theo goal và category
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserProfile profile = user.getUserProfile();
        if (profile == null || profile.getHealthGoal() == null) {
            throw new AppException(ErrorCode.USER_PROFILE_NULL);
        }

        Goal goal = profile.getHealthGoal();
        return exerciseRepository
                .findByGoalAndExerciseCategory_CategoryId(goal, categoryId)
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }


    @Caching(evict = {
            @CacheEvict(value = "exercisesByGoal", allEntries = true),
            @CacheEvict(value = "exercisesByGoalAndCategory", allEntries = true),
            @CacheEvict(value = "exercise_detail", key = "#exerciseId")
    })
    public ExerciseResponse updateExercise(Long exerciseId, UpdateExerciseRequest request) throws IOException {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));

        // category (DTO validate categoryId)
        ExerciseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_CATEGORY_NOT_FOUND));
        exercise.setExerciseCategory(category);

        // image optional: nếu gửi mới thì update, không gửi thì giữ ảnh cũ
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = fileUploadsService.uploadImage(request.getImage());
            exercise.setImageUrl(imageUrl);
        }

        // video optional
        if (request.getVideo() != null && !request.getVideo().isEmpty()) {
            String videoUrl = fileUploadsService.uploadVideo(request.getVideo());
            exercise.setVideoUrl(videoUrl);
        }

        // map thẳng (DTO đã validate)
        exercise.setExerciseName(request.getExerciseName());
        exercise.setCaloriesPerMinute(request.getCaloriesPerMinute());
        exercise.setDescription(request.getDescription());
        exercise.setInstructions(request.getInstructions());
        exercise.setDifficultyLevel(request.getDifficultyLevel());
        exercise.setGoal(request.getGoal());

        Exercise saved = exerciseRepository.save(exercise);
        return exerciseMapper.toResponse(saved);
    }


    @Caching(evict = {
            @CacheEvict(value = "exercisesByGoal", allEntries = true),
            @CacheEvict(value = "exercisesByGoalAndCategory", allEntries = true),
            @CacheEvict(value = "exercise_detail", key = "#exerciseId")
    })
    public void deleteExercise(Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));

        exerciseRepository.delete(exercise);
    }





}
