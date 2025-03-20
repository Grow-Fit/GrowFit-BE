package com.project.growfit.domain.auth.dto.response;

import com.project.growfit.domain.auth.entity.Parent;

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
                parent.getName(),
                parent.getProviderId(),
                parent.getProfileImage(),
                String.valueOf(parent.getRole())
        );
    }
}
