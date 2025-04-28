package com.project.growfit.domain.Diet.dto.request;
import com.project.growfit.domain.Diet.entity.MealType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "식단 수정 요청 DTO")
public record UpdateDietRequestDto(
        @NotBlank(message = "시간을 입력해주세요.")
        @Schema(description = "식사 시간 (HH:mm)", example = "08:30")
        String eatTime,

        @NotNull
        @Schema(description = "식사 종류", example = "BREAKFAST")
        MealType mealType,

        @NotNull
        @Schema(description = "수정할 음식 리스트")
        List<FoodItemDto> foodList
) {}