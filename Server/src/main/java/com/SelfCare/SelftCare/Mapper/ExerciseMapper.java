package com.SelfCare.SelftCare.Mapper;

import com.SelfCare.SelftCare.DTO.Response.ExerciseResponse;
import com.SelfCare.SelftCare.Entity.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = {ExerciseCategoryMapper.class})
public interface ExerciseMapper {
    @Mapping(source = "exerciseCategory", target = "category")
    ExerciseResponse toResponse(Exercise exercise);
}
