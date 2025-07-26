package com.project.growfit.domain.User.dto.response;

import com.project.growfit.domain.User.entity.Child;

public record ChildQrCodeResponseDto(
        Long child_id,
        String qr_code,
        String code
) {
    public static ChildQrCodeResponseDto toDto(Child child, String qr_code, String code) {
        return new ChildQrCodeResponseDto(
                child.getId(),
                qr_code,
                code
        );
    }
}
