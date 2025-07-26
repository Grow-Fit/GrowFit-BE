package com.project.growfit.domain.User.dto.request;

import com.project.growfit.domain.User.entity.ChildGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ParentInfoRequestDto(

        @NotBlank(message = "부모 닉네임은 필수입니다.")
        @Schema(description = "부모 닉네임", example = "민준맘")
        String nickname,

        @Email(message = "유효한 이메일 형식이어야 합니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        @Schema(description = "부모 이메일", example = "parent@example.com")
        String email,

        @NotBlank(message = "자녀 이름은 필수입니다.")
        @Schema(description = "자녀 이름", example = "김민준")
        String childName,

        @Min(value = 1, message = "자녀 나이는 1세 이상이어야 합니다.")
        @Schema(description = "자녀 나이", example = "11")
        int childAge,

        @NotNull(message = "자녀 성별은 필수입니다.")
        @Schema(description = "자녀 성별", example = "MALE")
        ChildGender childGender,

        @Schema(description = "아이 키 (cm)", example = "110")
        @Min(value = 30, message = "키는 30cm 이상이어야 합니다.")
        @Max(value = 250, message = "키는 200cm 이하이어야 합니다.")
        long childHeight,

        @Schema(description = "아이 몸무게 (kg)", example = "20")
        @Min(value = 5, message = "몸무게는 5kg 이상이어야 합니다.")
        @Max(value = 150, message = "몸무게는 150kg 이하이어야 합니다.")
        long childWeight
) {
}
