package com.project.growfit.domain.Goal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "칭찬 편지 작성 요청 DTO")
public record LetterRequestDto(

        @Schema(description = "편지 내용", example = "이번 주 목표를 모두 완성했어요! 정말 자랑스럽고 대견해요 :)")
        String content
) {
}
