package com.project.growfit.domain.Diet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "식단 영양소 수동 수정 요청 DTO")
public record UpdateNutritionRequestDto(
        @NotNull(message = "총 칼로리를 입력해주세요.")
        @Schema(description = "총 칼로리", example = "474")
        Double totalCalorie,

        @NotNull(message = "탄수화물을 입력해주세요.")
        @Schema(description = "탄수화물 (g)", example = "30")
        Double carbohydrate,

        @NotNull(message = "단백질을 입력해주세요.")
        @Schema(description = "단백질 (g)", example = "17")
        Double protein,

        @NotNull(message = "지방을 입력해주세요.")
        @Schema(description = "지방 (g)", example = "13")
        Double fat
) {}