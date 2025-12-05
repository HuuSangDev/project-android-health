package com.SelfCare.SelftCare.Entity;

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
    String difficultyLevel = "BEGINNER"; // MỨC ĐỘ: BEGINNER, INTERMEDIATE, ADVANCED
    String equipmentNeeded; // thiết bị cần dùng
    String muscleGroups; // nhóm cơ luyện tập
    String imageUrl; // URL ảnh
    String videoUrl; // URL video hướng dẫn

    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    ExerciseCategory exerciseCategory;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
// Bài tập - lưu trữ thông tin chi tiết về các bài tập