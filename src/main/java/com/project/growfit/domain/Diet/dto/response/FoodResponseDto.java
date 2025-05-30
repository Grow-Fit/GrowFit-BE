package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.global.api.entity.FoodApi;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "음식 응답 DTO")
public record FoodResponseDto(
        @Schema(description = "음식 이름", example = "닭가슴살")
        String foodName,

        @Schema(description = "대분류", example = "육류")
        String foodMainCategory,

        @Schema(description = "소분류", example = "가공육")
        String foodSubCategory,

        @Schema(description = "제조사", example = "하림")
        String manufacturerName,

        @Schema(description = "칼로리", example = "120")
        double calories,

        @Schema(description = "탄수화물", example = "0.5")
        double carbohydrates,

        @Schema(description = "지방", example = "1.5")
        double fat,

        @Schema(description = "단백질", example = "25.0")
        double protein
) {
    public static FoodResponseDto toDto(FoodApi foodApi) {
        return new FoodResponseDto(
                foodApi.getFoodNm(),
                foodApi.getFoodLv3Nm(),
                foodApi.getFoodLv4Nm(),
                foodApi.getMfrNm(),
                foodApi.getEnerc(),
                foodApi.getChocdf(),
                foodApi.getFatce(),
                foodApi.getProt()
        );
    }
}