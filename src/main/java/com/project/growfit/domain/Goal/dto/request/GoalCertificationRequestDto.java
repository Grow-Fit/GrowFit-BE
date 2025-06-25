package com.project.growfit.domain.Goal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "목표 인증 요청 DTO")
public record GoalCertificationRequestDto(
        @NotNull @Schema(description = "이미지 URL", example = "https://s3-url/image.jpg")
        String imageUrl
) {}
