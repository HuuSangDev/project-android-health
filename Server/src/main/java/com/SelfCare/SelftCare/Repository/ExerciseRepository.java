package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByExerciseCategory_CategoryId(Long categoryId);
}

