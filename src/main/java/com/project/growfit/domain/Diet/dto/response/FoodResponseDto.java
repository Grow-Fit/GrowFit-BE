package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.global.api.entity.FoodApi;

public record FoodResponseDto(
        String foodName,
        String foodMainCategory,
        String foodSubCategory,
        String manufacturerName,
        double calories,
        double carbohydrates,
        double fat,
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
