package com.project.growfit.domain.board.dto.response;

import com.project.growfit.domain.board.entity.Post;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record MyPageResponseListDto(
        Long postId,
        String title,
        String content,
        String createdAt,
        String firstImgUrl,
        int remainImgCnt
) {
    public static MyPageResponseListDto from(Post post, int imgCnt, String imgUrl) {
        return new MyPageResponseListDto(
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
