package com.project.growfit.domain.Diet.dto.response;

import java.util.List;

public record DietResponseDto(
        Long dietId,
        String time,
        List<FoodResponseDto> foods
) {
}
