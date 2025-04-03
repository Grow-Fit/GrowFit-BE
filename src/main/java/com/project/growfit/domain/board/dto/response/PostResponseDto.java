package com.project.growfit.domain.board.dto.response;

import com.project.growfit.domain.board.entity.Image;
import com.project.growfit.domain.board.entity.Post;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public record PostResponseDto(

    Long postId,
    String writer,
    String title,
    List<String> categoryAndAges,
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
                getCategoriesAndAges(post),
                post.getContent(),
                getFormattedDate(post.getCreatedAt()),
                post.getHits(),
                getImageUrls(post),
                likeCount,
                isLike,
                isBookmark
        );
    }

    private static List<String> getCategoriesAndAges(Post post) {
        List<String> result = new ArrayList<>();
        result.add(post.getCategory().name());
        result.addAll(post.getPostAges().stream()
                .map(postAge -> postAge.getAge().name())
                .toList());
        return result;
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
