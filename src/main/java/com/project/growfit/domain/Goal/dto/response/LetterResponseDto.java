package com.project.growfit.domain.Goal.dto.response;

import com.project.growfit.domain.Goal.entity.Letter;

import java.util.List;
import java.util.stream.Collectors;

public record LetterResponseDto(
        Long letterId,
        String content,
        List<LetterDetailDto> details
) {
    public static LetterResponseDto toDto(Letter letter) {
        List<LetterDetailDto> detailDtos = letter.getWeeklyGoal().getGoalList().stream()
                .filter(goal -> !goal.getCertificationList().isEmpty())
                .map(goal -> new LetterDetailDto(
                        goal.getId(),
                        goal.getCertificationList().get(0).getImageUrl()
                ))
                .collect(Collectors.toList());

        return new LetterResponseDto(
                letter.getId(),
                letter.getContent(),
                detailDtos
        );
    }

    public record LetterDetailDto(
            Long goalId,
            String imageUrl
    ) {
    }
}