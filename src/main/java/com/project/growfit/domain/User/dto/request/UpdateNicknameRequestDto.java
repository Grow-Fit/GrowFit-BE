package com.project.growfit.domain.User.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateNicknameRequestDto(
        @Schema(description = "닉네임 입력", example = "닉네임입니다")
        String nickname
) {
}
