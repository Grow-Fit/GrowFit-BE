package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.DietState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "식단 상세 응답 DTO")
public record DietDetailResponseDto(
        @Schema(description = "식단 ID", example = "11")
        Long dietId,

        @Schema(description = "사진 URL", example = "https://image-url.com")
        String imageUrl,

        @Schema(description = "섭취 시간", example = "08:30")
        String time,

        @Schema(description = "섭취 기록", example = "밥 반공기, 닭가슴살 100g")
        String foodLog,

        @Schema(description = "총 칼로리", example = "420")
        double totalCalorie,

        @Schema(description = "총 탄수화물", example = "30.5")
        double totalCarbohydrate,

        @Schema(description = "총 단백질", example = "25.0")
        double totalProtein,

        @Schema(description = "총 지방", example = "15.0")
        double totalFat,

        @Schema(description = "식단 상태")
        DietState state
) {}
