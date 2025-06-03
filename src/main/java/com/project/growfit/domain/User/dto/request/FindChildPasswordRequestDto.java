package com.project.growfit.domain.User.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record FindChildPasswordRequestDto(

        @Schema(description = "아이 로그인 ID", example = "child123")
        @NotBlank(message = "아이디를 입력해주세요.")
        String user_id,

        @Schema(description = "인증 코드", example = "ABC123")
        @NotBlank(message = "코드를 입력해주세요")
        String code,

        @Schema(description = "새로운 비밀번호", example = "newPassword123")
        @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
        String new_password
) {
}
