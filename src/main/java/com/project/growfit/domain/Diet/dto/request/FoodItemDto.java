package com.project.growfit.domain.Diet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Schema(description = "식단에 추가할 음식 항목 DTO")
public record FoodItemDto(
        @NotNull(message = "음식 ID를 입력해주세요.")
        @Schema(description = "음식 ID (공공 데이터 기반)", example = "1")
        Long foodId,

        @NotBlank(message = "음식 이름을 입력해주세요.")
        @Schema(description = "직접 등록한 음식 이름", example = "닭가슴살")
        String name,

        @NotNull(message = "칼로리를 입력해주세요.")
        @Schema(description = "칼로리 (kcal)", example = "120")
        Double kcal,

        @NotNull(message = "탄수화물을 입력해주세요.")
        @Schema(description = "탄수화물 (g)", example = "0.5")
        Double carbohydrate,

        @NotNull(message = "지방을 입력해주세요.")
        @Schema(description = "지방 (g)", example = "1.5")
        Double fat,

        @NotNull(message = "단백질을 입력해주세요.")
        @Schema(description = "단백질 (g)", example = "25")
        Double protein,

        @Schema(description = "수량", example = "1")
        int count
) {}