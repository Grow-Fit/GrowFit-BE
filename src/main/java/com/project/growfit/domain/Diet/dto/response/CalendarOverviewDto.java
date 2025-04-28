package com.project.growfit.domain.Diet.dto.response;

public record CalendarOverviewDto(
        String date,
        boolean hasDiet,
        boolean hasSticker
) {
}
