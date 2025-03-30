package com.project.growfit.domain.board.dto.response;

import com.project.growfit.domain.board.entity.Age;
import com.project.growfit.domain.board.entity.Category;
import com.project.growfit.domain.board.entity.Image;
import com.project.growfit.domain.board.entity.Post;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record PostResponseDto(

    Long postId,
    String writer,
    String title,
    Category category,
    Age age,
    String content,
    String createdAt,
    int hits,
    List<String> imageUrls,
    int likeCount,
    boolean isLike,
    boolean isBookmark
){

    public static PostResponseDto from(Post post, String writer, int likeCount, boolean isLike, boolean isBookmark) {
        return new PostResponseDto(
                post.getId(),
                writer,
                post.getTitle(),
                post.getCategory(),
                post.getAge(),
                post.getContent(),
                getFormattedDate(post.getCreatedAt()),
                post.getHits(),
                getImageUrls(post),
                likeCount,
                isLike,
                isBookmark
        );
    }

    private static List<String> getImageUrls(Post post) {
        return post.getImageList().stream()
                .map(Image::getImageUrl)
                .toList();
    }

    public static String getFormattedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
