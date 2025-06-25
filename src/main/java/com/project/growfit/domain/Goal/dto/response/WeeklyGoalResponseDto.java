package com.project.growfit.domain.Goal.dto.response;

import com.project.growfit.domain.Goal.entity.GoalStatus;
import com.project.growfit.domain.Goal.entity.WeeklyGoal;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "주간 목표 조회 응답 DTO")
public record WeeklyGoalResponseDto(
        Long weeklyGoalId,
        LocalDate startDate,
        LocalDate endDate,
        boolean isLetterSent,
        int targetCount,
        List<GoalResponseDto> goals
) {
    public static WeeklyGoalResponseDto toDto(WeeklyGoal weeklyGoal) {
        List<GoalResponseDto> goals =
                weeklyGoal.getGoalList().stream().map(
                                goal -> new GoalResponseDto(
                                        goal.getId(),
                                        goal.getName(),
                                        goal.getIconId(),
                                        goal.getCertificationList().size(),
                                        goal.getStatus()

                                ))
                        .toList();

        return new WeeklyGoalResponseDto(
                weeklyGoal.getId(),
                weeklyGoal.getStartDate(),
                weeklyGoal.getEndDate(),
                weeklyGoal.isLetterSent(),
                weeklyGoal.getCertificationCount(),
                goals
        );
    }
}