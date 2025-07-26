package com.project.growfit.domain.User.dto.response;

import com.project.growfit.domain.User.entity.Child;

public record ChildResponseDto(
        Long child_id,
        String child_name,
        String child_login_id
) {
    public static ChildResponseDto toDto(Child child, String login_id) {
        return new ChildResponseDto(
                child.getId(),
                child.getName(),
                login_id
        );
    }

    public static ChildResponseDto toDto(Child child) {
        return new ChildResponseDto(
                child.getId(),
                child.getName(),
                child.getLoginId()
        );
    }
}