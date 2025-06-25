package com.project.growfit.domain.Goal.dto.response;

import com.project.growfit.domain.Goal.entity.Goal;
import com.project.growfit.domain.Goal.entity.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개별 목표 응답 DTO")
public record GoalResponseDto(
        Long goalId,
        String name,
        int iconId,
        int certifiedCount,
        GoalStatus status
) {
    public static GoalResponseDto toDto(Goal goal) {
        return new GoalResponseDto(
                goal.getId(),
                goal.getName(),
                goal.getIconId(),
                goal.getCertificationList().size(),
                goal.getStatus()
        );
    }
}