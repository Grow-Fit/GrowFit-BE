package com.project.growfit.domain.auth.dto.request;

import com.project.growfit.domain.User.entity.ChildGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthParentRequestDto(
        @Schema(description = "등록할 부모 닉네임", example = "건강한 엄마")
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname,

        @Schema(description = "아이 이름", example = "김민준")
        @NotBlank(message = "아이 이름을 입력해주세요.")
        String child_name,

        @Schema(description = "아이 성별", example = "MALE")
        @NotNull(message = "성별을 선택해주세요.")
        ChildGender child_gender,

        @Schema(description = "아이 나이", example = "5")
        @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
        int child_age,

        @Schema(description = "아이 키 (cm)", example = "110")
        @Min(value = 30, message = "키는 30cm 이상이어야 합니다.")
        long child_height,

        @Schema(description = "아이 몸무게 (kg)", example = "20")
        @Min(value = 5, message = "몸무게는 5kg 이상이어야 합니다.")
        long child_weight
) {
}
