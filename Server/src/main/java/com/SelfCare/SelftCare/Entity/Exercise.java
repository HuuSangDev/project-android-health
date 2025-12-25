package com.SelfCare.SelftCare.Entity;

import com.SelfCare.SelftCare.Enum.Goal;
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
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long exerciseId;

    String exerciseName; // tên bài tập
    Double caloriesPerMinute; // calo đốt cháy mỗi phút
    String description; // mô tả bài tập
    String instructions; // hướng dẫn
    @Builder.Default
    String difficultyLevel = "BEGINNER"; // MỨC ĐỘ: BEGINNER, INTERMEDIATE, ADVANCED
    String imageUrl; // URL ảnh
    String videoUrl; // URL video hướng dẫn

    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    ExerciseCategory exerciseCategory;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    Goal goal;
}
