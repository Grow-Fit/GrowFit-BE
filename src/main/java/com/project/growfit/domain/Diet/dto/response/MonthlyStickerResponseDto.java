package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.Sticker;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "월별 스티커 응답 DTO")
public record MonthlyStickerResponseDto(
        @Schema(description = "아이 이름", example = "민지")
        String childName,

        @Schema(description = "날짜별 스티커 정보")
        Map<String, Map<String, Sticker>> monthlyStickers
) {}
