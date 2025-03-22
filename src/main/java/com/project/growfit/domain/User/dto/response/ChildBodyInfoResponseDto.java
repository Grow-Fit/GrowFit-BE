package com.project.growfit.domain.User.dto.response;

import com.project.growfit.domain.User.entity.ChildBodyInfo;
import java.time.LocalDateTime;

public record ChildBodyInfoResponseDto(
        Long id,
        long height,
        long weight,
        double bmi,
        LocalDateTime createdAt) {
    public static ChildBodyInfoResponseDto toDto(ChildBodyInfo bodyInfo) {
        return new ChildBodyInfoResponseDto(
                bodyInfo.getId(),
                bodyInfo.getHeight(),
                bodyInfo.getWeight(),
                bodyInfo.getBmi(),
                bodyInfo.getCreatedAt()
        );
    }

}