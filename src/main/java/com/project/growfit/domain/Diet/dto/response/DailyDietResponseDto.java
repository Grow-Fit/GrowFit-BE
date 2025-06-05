package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.MealType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "하루 식단 응답 DTO")
public record DailyDietResponseDto(
        @Schema(description = "DailyDiet ID", example = "1")
        Long dailyDietId,

        @Schema(description = "식단 날짜", example = "2025-04-10")
        String date,

        @Schema(description = "총 칼로리", example = "950")
        double totalCalorie,

        @Schema(description = "식사 유형별 식단 목록")
        Map<MealType, List<DietResponseDto>> dietsByMealType
) {}