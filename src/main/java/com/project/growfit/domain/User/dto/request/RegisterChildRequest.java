package com.project.growfit.domain.auth.dto.request;

import com.project.growfit.domain.auth.entity.GENDER;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterChildRequest(
        @Schema(description = "아이 이름", example = "김민준")
        String child_name,
        @Schema(description = "아이 성별", example = "MALE")
        GENDER child_gender,
        @Schema(description = "아이 나이", example = "5")
        int child_age,
        @Schema(description = "아이 키 (cm)", example = "110")
        long child_height,
        @Schema(description = "아이 몸무게 (kg)", example = "20")
        long child_weight
) {
}
