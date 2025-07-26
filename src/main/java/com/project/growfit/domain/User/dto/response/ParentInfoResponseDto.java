package com.project.growfit.domain.User.dto.response;

import com.project.growfit.domain.User.entity.Parent;

public record ParentInfoResponseDto(
        String nickname,
        String profileImage,
        ChildInfoResponseDto child
) {
    public static ParentInfoResponseDto toDto(Parent parent) {
        return new ParentInfoResponseDto(
                parent.getNickname(),
                parent.getProviderId(),
                ChildInfoResponseDto.toDto(parent.getChildren().get(0))
        );
    }
}
