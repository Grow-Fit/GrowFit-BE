package com.project.growfit.domain.Diet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "식단에 추가할 음식 항목")
public record FoodItemDto(
        @Schema(description = "음식 ID (기존 API 기반)", example = "1")
        Long foodId,

        @Schema(description = "음식 이름 (직접 등록 시 사용)", example = "닭가슴살")
        String name,

        @Schema(description = "칼로리(kcal)", example = "120")
        Double kcal,

        @Schema(description = "탄수화물(g)", example = "0.5")
        Double carbohydrate,

        @Schema(description = "지방(g)", example = "1.5")
        Double fat,

        @Schema(description = "단백질(g)", example = "25")
        Double protein
) {}