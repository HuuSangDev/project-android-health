package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.CreateExerciseRequest;
import com.SelfCare.SelftCare.DTO.Request.UpdateExerciseRequest;
import com.SelfCare.SelftCare.DTO.Response.ExerciseResponse;
import com.SelfCare.SelftCare.Service.ExerciseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/exercises")
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseController {

    ExerciseService exerciseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    ApiResponse<ExerciseResponse> createExercise( @ModelAttribute  @Valid CreateExerciseRequest request,
                                                  @RequestPart(value = "image", required = false) MultipartFile image,
                                                  @RequestPart(value = "video", required = false) MultipartFile video
                                                 ) throws IOException {
        request.setImage(image);
        request.setVideo(video);
        return ApiResponse.<ExerciseResponse>builder()
                .result(exerciseService.createExercise(request))
                .message("Create exercise success")
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<ExerciseResponse>> AllExercisesByUserGoal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        return ApiResponse.<List<ExerciseResponse>>builder()
                .result(exerciseService.getAllExercisesByUserGoal(email, isAdmin))
                .message("Danh sách bài tập")
                .build();
    }


    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ExerciseResponse>> getExercisesByCategory(@PathVariable Long categoryId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        return ApiResponse.<List<ExerciseResponse>>builder()
                .result(exerciseService.getExercisesByCategory(email, categoryId, isAdmin))
                .message("Danh sách bài tập")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{exerciseId}", consumes = "multipart/form-data")
    public ApiResponse<ExerciseResponse> updateExercise(
            @PathVariable Long exerciseId,
            @Valid @ModelAttribute UpdateExerciseRequest request
    ) throws IOException {
        return ApiResponse.<ExerciseResponse>builder()
                .message("update exercise success")
                .result(exerciseService.updateExercise(exerciseId, request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{exerciseId}")
    public ApiResponse<Void> deleteExercise(@PathVariable Long exerciseId) {
        exerciseService.deleteExercise(exerciseId);
        return ApiResponse.<Void>builder()
                .message("delete exercise success")
                .build();
    }

    @GetMapping("/{exerciseId}")
    public ApiResponse<ExerciseResponse> getExerciseById(@PathVariable Long exerciseId) {
        return ApiResponse.<ExerciseResponse>builder()
                .result(exerciseService.getExerciseById(exerciseId))
                .message("Chi tiết bài tập")
                .build();
    }


}
