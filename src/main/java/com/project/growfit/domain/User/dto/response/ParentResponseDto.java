package com.project.growfit.domain.User.dto.response;


import com.project.growfit.domain.User.entity.Parent;

public record ParentResponseDto(
        String email,
        String name,
        String kakaoIdentifier,
        String profileImage,
        String roles
) {
    public ParentResponseDto(Parent parent) {
        this(
                parent.getEmail(),
                parent.getNickname(),
                parent.getProviderId(),
                parent.getPhoto(),
                String.valueOf(parent.getRole())
        );
    }
}
