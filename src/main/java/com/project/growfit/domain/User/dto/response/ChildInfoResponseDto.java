package com.project.growfit.domain.User.dto.response;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.ChildGender;

public record ChildInfoResponseDto(
        Long id,
        String code,
        String child_name,
        ChildGender child_gender,
        int child_age,
        ChildBodyInfoResponseDto child_BodyInfo
) {
    public static ChildInfoResponseDto toDto(Child child) {
        return new ChildInfoResponseDto(
                child.getId(),
                child.getCodeNumber(),
                child.getName(),
                child.getGender(),
                child.getAge(),
                ChildBodyInfoResponseDto.toDto(child.getLatestBodyInfo())
        );
    }
}
