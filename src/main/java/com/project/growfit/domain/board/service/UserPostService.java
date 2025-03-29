package com.project.growfit.domain.board.service;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.board.dto.response.BookmarkResponseListDto;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.domain.board.repository.ImageRepository;
import com.project.growfit.domain.board.repository.PostRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPostService {

    private final ParentRepository parentRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    public List<BookmarkResponseListDto> getBookmarks(Long userId) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(
                ErrorCode.USER_NOT_FOUND));

        if (parent.getId().equals(userId)) {
            List<Post> bookmarkPosts = postRepository.findBookmarkPostsByUserId(userId);
            return bookmarkPosts.stream()
                    .map(post -> {
                        int imgCount = imageRepository.countImagesByPostId(post.getId());
                        if (imgCount != 0) imgCount -= 1;
                        String firstImgUrl = imageRepository.findFirstImageUrlByPostId(post.getId());
                        return BookmarkResponseListDto.from(post, imgCount, firstImgUrl);
                    }).collect(Collectors.toList());
        }
        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
    }

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getEmail();
    }
}
