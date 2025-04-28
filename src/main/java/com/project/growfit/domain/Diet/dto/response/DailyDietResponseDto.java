package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.MealType;

import java.util.List;
import java.util.Map;

public record DailyDietResponseDto(
        Long dailyDietId,
        String date,
        Map<MealType, List<DietResponseDto>> dietsByMealType

) {
}
