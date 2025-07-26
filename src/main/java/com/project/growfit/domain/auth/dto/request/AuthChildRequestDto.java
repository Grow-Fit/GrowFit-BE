package com.project.growfit.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthChildRequestDto(
        @Schema(description = "아이 로그인 ID", example = "child123")
        @NotBlank(message = "아이디를 입력해주세요.")
        @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
        String childId,

        @Schema(description = "아이 로그인 비밀번호", example = "password123")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d@$!%*?&]{8,}$",
                message = "비밀번호는 소문자와 숫자를 포함하여 8자 이상이어야 합니다.")
        String childPassword
) {
}
