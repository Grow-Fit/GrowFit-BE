package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.DietSet;

public record DietSetBasicDto(
        Long dietSetId,
        String setName,
        double totalCalorie
) {
    public static DietSetBasicDto toDto(DietSet set) {
        return new DietSetBasicDto(
                set.getId(),
                set.getSetName(),
                set.getTotalCalorie()
        );
    }
}