package com.project.growfit.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthChildRequestDto(

        @Schema(description = "아이 로그인 ID", example = "child123")
        @NotBlank(message = "아이디를 입력해주세요.")
        String childId,

        @Schema(description = "아이 로그인 비밀번호", example = "password123")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문자와 숫자를 포함해야 합니다.")
        String childPassword,

        @Schema(description = "닉네임 입력", example = "민준콩")
        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname
) {
}