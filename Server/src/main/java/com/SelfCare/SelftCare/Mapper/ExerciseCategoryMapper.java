package com.SelfCare.SelftCare.Mapper;

import com.SelfCare.SelftCare.DTO.Response.ExerciseCategoryResponse;
import com.SelfCare.SelftCare.Entity.ExerciseCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseCategoryMapper {

    ExerciseCategoryResponse toResponse(ExerciseCategory category);
}
