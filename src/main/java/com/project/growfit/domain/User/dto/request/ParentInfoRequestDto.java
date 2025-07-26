package com.project.growfit.domain.User.dto.request;

import com.project.growfit.domain.User.entity.ChildGender;
import io.swagger.v3.oas.annotations.media.Schema;

public record ParentInfoRequestDto(
        @Schema(description = "부모 닉네임", example = "민준맘")
        String nickname,

        @Schema(description = "부모 이메일", example = "parent@example.com")
        String email,

        @Schema(description = "자녀 이름", example = "김민준")
        String childName,

        @Schema(description = "자녀 나이", example = "11")
        int childAge,

        @Schema(description = "자녀 성별", example = "MALE")
        ChildGender childGender,

        @Schema(description = "자녀 키(cm)", example = "145")
        long childHeight,

        @Schema(description = "자녀 몸무게(kg)", example = "38")
        long childWeight
) {
}
