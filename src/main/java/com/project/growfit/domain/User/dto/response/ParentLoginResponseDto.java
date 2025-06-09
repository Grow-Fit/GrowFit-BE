package com.project.growfit.domain.User.dto.response;

public record ParentLoginResponseDto(
        String email,
        boolean isNewUser
) {
}
