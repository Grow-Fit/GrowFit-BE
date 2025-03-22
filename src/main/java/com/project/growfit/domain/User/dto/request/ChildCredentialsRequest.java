package com.project.growfit.domain.User.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChildCredentialsRequest(
        @Schema(description = "아이 로그인 ID", example = "child123")
        String childId,
        @Schema(description = "아이 로그인 비밀번호", example = "password123")
        String childPassword
) {
}
