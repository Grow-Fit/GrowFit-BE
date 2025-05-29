package com.project.growfit.domain.Diet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "식단 세트 목록 응답 DTO")
public record DietSetResponseDto(
        @Schema(description = "식단 세트 ID", example = "2")
        Long dietSetId,

        @Schema(description = "식단 세트 이름", example = "저탄고지")
        String setName,

        @Schema(description = "총 칼로리", example = "820")
        double totalCalorie,

        @Schema(description = "음식 이름 리스트")
        List<String> foodNames
) {}