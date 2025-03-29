package com.project.growfit.domain.board.dto.response;

import com.project.growfit.domain.board.entity.Post;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record BookmarkResponseListDto(
        Long postId,
        String title,
        String content,
        String createdAt,
        String imgUrl,
        int remainImgCnt
) {
    public static BookmarkResponseListDto from(Post post, int imgCnt, String imgUrl) {
        return new BookmarkResponseListDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                getFormattedDate(post.getCreatedAt()),
                imgUrl,
                imgCnt
        );
    }

    public static String getFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
