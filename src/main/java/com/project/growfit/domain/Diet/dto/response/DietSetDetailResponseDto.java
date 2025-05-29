package com.project.growfit.domain.Diet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "식단 세트 상세 응답 DTO")
public record DietSetDetailResponseDto(
        @Schema(description = "식단 세트 이름", example = "고단백 식단")
        String setName,

        @Schema(description = "음식 세부 정보 리스트")
        List<FoodResponseDto> foodDetails
) {}