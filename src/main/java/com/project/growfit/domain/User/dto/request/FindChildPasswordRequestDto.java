package com.project.growfit.domain.User.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record FindChildPasswordRequestDto(
        @Schema(description = "아이 로그인 ID", example = "child123")
        String user_id,
        String code,

        @Schema(description = "새로운 비밀번호", example = "newPassword123")
        String new_password
) {
}
