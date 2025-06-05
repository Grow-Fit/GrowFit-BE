package com.project.growfit.domain.Diet.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateFoodListRequestDto(
        @NotNull(message = "음식을 입력해주세요.")
        @Size(min = 1, message = "음식은 최소 한 개 이상 입력해주세요.")
        List<FoodItemDto> foodList
) {}