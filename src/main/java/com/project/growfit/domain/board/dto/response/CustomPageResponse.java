package com.project.growfit.domain.board.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record CustomPageResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean isFirst,
        boolean isLast
) {
    public static <T> CustomPageResponse<T> from(Page<T> page) {
        return new CustomPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <T> CustomPageResponse<T> from(List<T> dtoList, Page<?> page) {
        return new CustomPageResponse<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }
}
