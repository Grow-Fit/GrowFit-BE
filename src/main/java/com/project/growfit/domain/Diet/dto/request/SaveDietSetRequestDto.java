package com.project.growfit.domain.Diet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "식단 세트 저장 요청 DTO")
public record SaveDietSetRequestDto(
        @NotBlank(message = "세트 이름을 입력해주세요.")
        @Schema(description = "세트 이름", example = "다이어트 식단 세트")
        String setName,

        @Valid
        @Schema(description = "음식 리스트")
        @NotNull(message = "음식을 입력해주세요.")
        @Size(min = 1, message = "음식은 최소 한 개 이상 입력해주세요.")
        List<FoodItemDto> foodList
) {}