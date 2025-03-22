package com.project.growfit.domain.auth.dto.response;

import com.project.growfit.domain.auth.entity.Child;
import com.project.growfit.domain.auth.entity.GENDER;

public record ChildInfoResponseDto(
        Long id,
        String code,
        String child_name,
        GENDER child_gender,
        int child_age,
        long child_height,
        long child_weight
) {
    public static ChildInfoResponseDto toDto(Child child) {
        return new ChildInfoResponseDto(
                child.getPid(),
                child.getCode(),
                child.getChildName(),
                child.getChildGender(),
                child.getChildAge(),
                child.getChildHeight(),
                child.getChildWeight()
        );
    }
}
