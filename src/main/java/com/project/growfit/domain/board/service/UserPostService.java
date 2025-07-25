package com.project.growfit.domain.board.service;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.board.dto.request.ProfileRequestDto;
import com.project.growfit.domain.board.dto.response.MyInfoResponseDto;
import com.project.growfit.domain.board.dto.response.MyPageResponseListDto;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.domain.board.repository.ImageRepository;
import com.project.growfit.domain.board.repository.PostRepository;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.s3.service.S3UploadService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPostService {

    private final ParentRepository parentRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final S3UploadService s3UploadService;

    private String imageUploadPath = "profile/";

    @Transactional(readOnly = true)
    public List<MyPageResponseListDto> getBookmarks(Long userId) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(
                ErrorCode.USER_NOT_FOUND));

        if (parent.getId().equals(userId)) {
            List<Post> bookmarkPosts = postRepository.findBookmarkPostsByUserId(userId);
            return getMyPageResponseListDtos(bookmarkPosts);
        }
        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
    }

    private List<MyPageResponseListDto> getMyPageResponseListDtos(List<Post> posts) {
        return posts.stream()
                .map(post -> {
                    int imgCount = imageRepository.countImagesByPostId(post.getId());
                    if (imgCount != 0) imgCount -= 1;
                    String firstImgUrl = imageRepository.findFirstImageUrlByPostId(post.getId());
                    return MyPageResponseListDto.from(post, imgCount, firstImgUrl);
                }).collect(Collectors.toList());
    }

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getEmail();
    }

    @Transactional(readOnly = true)
    public List<MyPageResponseListDto> getMyPosts(Long userId) {
        List<Post> myPosts = postRepository.findPostsByUserId(userId);
        return getMyPageResponseListDtos(myPosts);
    }

    @Transactional(readOnly = true)
    public MyInfoResponseDto getProfile(Long userId) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(
                ErrorCode.USER_NOT_FOUND));
        boolean isSelf = false;
        if (parent.getId().equals(userId)) {
            isSelf = true;
        }
        return MyInfoResponseDto.from(parent, isSelf);
    }

    @Transactional
    public void updateProfile(ProfileRequestDto dto, MultipartFile image) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(
                ErrorCode.USER_NOT_FOUND));

        if (parent.getPhoto() != null) {
            s3UploadService.deleteFile(parent.getPhoto());
            parent.deletedPhoto();
        }

        if (image != null) {
            parent.updateCommunityInfo(dto, s3UploadService.saveFile(image, imageUploadPath));
            return;
        }
        parent.updateCommunityInfo(dto, null);
    }
}
