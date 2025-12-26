package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.Exercise;
import com.SelfCare.SelftCare.Enum.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByGoal(Goal goal);

    // theo goal + category
    List<Exercise> findByGoalAndExerciseCategory_CategoryId(
            Goal goal,
            Long categoryId
    );
    
    // theo category only (cho admin)
    List<Exercise> findByExerciseCategory_CategoryId(Long categoryId);
}

