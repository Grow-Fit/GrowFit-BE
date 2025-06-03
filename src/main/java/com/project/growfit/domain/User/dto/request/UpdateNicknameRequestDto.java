package com.project.growfit.domain.User.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequestDto(
        @Schema(description = "등록할 부모 닉네임", example = "건강한 엄마")
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname
) {
}