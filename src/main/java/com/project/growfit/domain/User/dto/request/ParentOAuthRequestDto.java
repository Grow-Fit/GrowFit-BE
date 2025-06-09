package com.project.growfit.domain.User.dto.request;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ParentOAuthRequestDto(
        @Schema(description = "이메일", example = "example@naver.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        String email,

        @Schema(description = "닉네임", example = "해윤맘")
        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname,

        @Schema(description = "소셜 고유 ID", example = "kakao_1234567890")
        @NotBlank(message = "소셜 ID를 입력해주세요.")
        String id
) {
    public Parent toEntity(String email, String nickname, String id) {
        return new Parent(email, nickname, null, "kakao", id, ROLE.fromString("parent"));
    }
}