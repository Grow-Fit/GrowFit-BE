package com.project.growfit.domain.Goal.dto.response;

import com.project.growfit.domain.Goal.entity.GoalStatus;
import com.project.growfit.domain.Goal.entity.WeeklyGoal;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "주간 달성 이력 응답 DTO")
public record WeeklyGoalHistoryResponseDto(
        LocalDate startDate,
        LocalDate endDate,
        List<GoalHistory> goals
) {
    public record GoalHistory(
            String name,
            GoalStatus status,
            int totalDays,
            int certifiedDays
    ) {}
}