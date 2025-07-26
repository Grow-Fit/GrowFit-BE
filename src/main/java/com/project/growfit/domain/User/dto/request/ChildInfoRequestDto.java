package com.project.growfit.domain.User.dto.request;

import com.project.growfit.domain.User.entity.ChildGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ChildInfoRequestDto(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Schema(description = "자녀 닉네임", example = "민준이")
        String nickname,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$",
                message = "비밀번호는 소문자와 숫자를 포함해 8자 이상이어야 합니다."
        )
        @Schema(description = "자녀 비밀번호", example = "secure123")
        String password,

        @NotNull(message = "성별을 입력해주세요.")
        @Schema(description = "자녀 성별", example = "MALE")
        ChildGender gender,

        @Min(value = 1, message = "나이는 1세 이상이어야 합니다.")
        @Schema(description = "자녀 나이", example = "11")
        int age,

        @Schema(description = "아이 키 (cm)", example = "110")
        @Min(value = 30, message = "키는 30cm 이상이어야 합니다.")
        @Max(value = 250, message = "키는 200cm 이하이어야 합니다.")
        long height,

        @Schema(description = "아이 몸무게 (kg)", example = "20")
        @Min(value = 5, message = "몸무게는 5kg 이상이어야 합니다.")
        @Max(value = 150, message = "몸무게는 150kg 이하이어야 합니다.")
        long weight
) {
}
