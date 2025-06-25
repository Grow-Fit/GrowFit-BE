package com.project.growfit.domain.Goal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "주간 목표 생성 요청 DTO")
public record CreateWeeklyGoalRequestDto(
        @NotNull
        @Schema(description = "시작 날짜", example = "2025-06-24")
        LocalDate startDate,

        @NotNull
        @Schema(description = "종료 날짜", example = "2025-06-30")
        LocalDate endDate,

        @NotNull
        @Min(value = 1, message = "인증 횟수는 최소 1회 이상이어야 합니다.")
        @Max(value = 7, message = "인증 횟수는 최대 7회까지 가능합니다.")
        @Schema(description = "목표 달성 설정 갯수", example = "2")
        int certificationCount,

        @Valid
        @Size(min = 1, max = 10, message = "목표는 최소 1개 이상, 최대 10개까지 설정 가능합니다.")
        @NotEmpty(message = "목표 목록은 비어 있을 수 없습니다.")
        @Schema(description = "목표 목록")
        List<GoalItem> goals
) {
    public record GoalItem(
            @NotNull @Schema(description = "목표 이름", example = "매일 독서 20분")
            String name,

            @NotNull @Schema(description = "아이콘 ID", example = "1")
            int iconId
    ) {}
}