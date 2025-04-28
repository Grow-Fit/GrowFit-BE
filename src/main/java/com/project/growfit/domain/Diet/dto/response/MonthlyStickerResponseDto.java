package com.project.growfit.domain.Diet.dto.response;

import com.project.growfit.domain.Diet.entity.Sticker;

import java.util.List;
import java.util.Map;

public record MonthlyStickerResponseDto(
        String childName,
        Map<String, Map<String, Sticker>> monthlyStickers
) {}