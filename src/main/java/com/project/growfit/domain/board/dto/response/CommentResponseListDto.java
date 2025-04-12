package com.project.growfit.domain.board.dto.response;

import com.project.growfit.domain.board.entity.Comment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record CommentResponseListDto(
        Long commentId,
        Long writerId,
        String writer,
        String createdAt,
        String content
) {
    public static CommentResponseListDto from(Comment comment) {
        return new CommentResponseListDto(
                comment.getId(),
                comment.getParent().getId(),
                comment.getParent().getNickname(),
                getFormattedDate(comment.getCreatedAt()),
                comment.getContent()
        );
    }

    public static String getFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
