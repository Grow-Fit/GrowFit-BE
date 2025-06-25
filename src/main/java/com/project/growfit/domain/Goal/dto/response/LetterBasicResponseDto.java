package com.project.growfit.domain.Goal.dto.response;

import com.project.growfit.domain.Goal.entity.Letter;
import com.project.growfit.domain.Goal.entity.WeeklyGoal;

import java.util.List;

public record LetterBasicResponseDto(
        Long letterId,
        Long weeklyGoalId,
        Long parentId,
        String content
) {
    public static LetterBasicResponseDto toDto(Letter letter) {
        return new LetterBasicResponseDto(
                letter.getId(),
                letter.getWeeklyGoal().getId(),
                letter.getWeeklyGoal().getParent().getId(),
                letter.getContent()
        );
    }
}