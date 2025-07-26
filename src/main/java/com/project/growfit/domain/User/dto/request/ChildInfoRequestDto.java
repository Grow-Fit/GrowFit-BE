package com.project.growfit.domain.User.dto.request;

import com.project.growfit.domain.User.entity.ChildGender;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChildInfoRequestDto(
        @Schema(description = "자녀 닉네임", example = "민준이")
        String nickname,

        @Schema(description = "자녀 비밀번호", example = "securePassword123")
        String password,

        @Schema(description = "자녀 성별", example = "MALE")
        ChildGender gender,

        @Schema(description = "자녀 나이", example = "11")
        int age,

        @Schema(description = "자녀 키(cm)", example = "145")
        long height,

        @Schema(description = "자녀 몸무게(kg)", example = "38")
        long weight
) {
}
