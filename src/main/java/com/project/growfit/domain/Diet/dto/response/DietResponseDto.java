package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.DietState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "식단 응답 DTO")
public record DietResponseDto(
        @Schema(description = "식단 ID", example = "5")
        Long dietId,
        @Schema(description = "사진 URL", example = "https://image-url.com")
        String imageUrl,

        @Schema(description = "섭취 시간", example = "12:30")
        String time,

        @Schema(description = "식단 상태")
        DietState state,

        @Schema(description = "음식 리스트")
        List<FoodResponseDto> foods,

        @Schema(description = "총 칼로리", example = "420")
        double totalCalorie,

        @Schema(description = "총 탄수화물", example = "30.5")
        double totalCarbohydrate,

        @Schema(description = "총 단백질", example = "25.0")
        double totalProtein,

        @Schema(description = "총 지방", example = "15.0")
        double totalFat

) {}
