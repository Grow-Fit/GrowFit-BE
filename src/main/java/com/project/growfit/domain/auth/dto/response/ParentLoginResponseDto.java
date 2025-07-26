package com.project.growfit.domain.auth.dto.response;

public record ParentLoginResponseDto(
        String email,
        boolean isNewUser
) {
}
