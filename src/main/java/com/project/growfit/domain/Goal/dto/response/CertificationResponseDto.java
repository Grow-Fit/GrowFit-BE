package com.project.growfit.domain.Goal.dto.response;

import com.project.growfit.domain.Goal.entity.Certification;

import java.time.LocalDateTime;

public record CertificationResponseDto(
        Long id,
        String imageUrl,
        LocalDateTime certifiedAt
) {
    public static CertificationResponseDto toDto(Certification certification) {
        return new CertificationResponseDto(
                certification.getId(),
                certification.getImageUrl(),
                certification.getCertifiedAt()
        );
    }
}
