package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.Diet;
import com.project.growfit.domain.Diet.entity.DietState;
import com.project.growfit.domain.Diet.entity.MealType;

public record DietBasicDto(
        Long dietId,
        Long childId,
        MealType mealType,
        String time,
        DietState state
) {
    public static DietBasicDto toDto(Diet diet) {
        return new DietBasicDto(diet.getId(),
                diet.getChild().getId(),
                diet.getMealType(),
                diet.getTime().toString(),
                diet.getState());
    }
}