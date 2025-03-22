package com.project.growfit.domain.User.dto.response;


import com.project.growfit.domain.User.entity.Parent;

public record ParentResponse(
        String email,
        String name,
        String kakaoIdentifier,
        String profileImage,
        String roles
) {
    public ParentResponse(Parent parent) {
        this(
                parent.getEmail(),
                parent.getNickname(),
                parent.getProviderId(),
                parent.getPhoto(),
                String.valueOf(parent.getRole())
        );
    }
}
