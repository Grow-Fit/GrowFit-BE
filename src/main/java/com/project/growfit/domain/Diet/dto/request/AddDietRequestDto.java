package com.project.growfit.domain.Diet.dto.request;

import com.project.growfit.domain.Diet.entity.MealType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "식단 등록 요청 DTO")
public record AddDietRequestDto(
        @NotBlank(message = "날짜를 입력해주세요.")
        @Schema(description = "식사 날짜 (yyyy-MM-dd)", example = "2025-04-06")
        String date,
        @NotBlank(message = "시간을 입력해주세요.")
        @Schema(description = "식사 시간 (HH:mm)", example = "08:30")
        String eatTime,
        @NotBlank(message = "식사 유형을 입력해주세요.")
        @Schema(description = "식사 유형", example = "BREAKFAST")
        MealType mealType,
        @NotBlank(message = "음식을 입력해주세요.")
        @Schema(description = "음식 리스트")
        List<FoodItemDto> foodList
) {
}
