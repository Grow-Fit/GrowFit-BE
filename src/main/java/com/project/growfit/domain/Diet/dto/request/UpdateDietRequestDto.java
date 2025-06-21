package com.project.growfit.domain.Diet.dto.request;
import com.project.growfit.domain.Diet.entity.MealType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "식단 수정 요청 DTO")
public record UpdateDietRequestDto(
        @Schema(description = "식사 시간 (HH:mm)", example = "08:30")
        String eatTime,

        @Schema(description = "식사 종류", example = "BREAKFAST")
        MealType mealType,

        @Schema(description = "수정 음식 리스트")
        @Size(min = 1, message = "음식은 최소 한 개 이상 입력해주세요.")
        List<FoodItemDto> foodList
) {}