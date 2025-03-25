package com.project.growfit.domain.board.service;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.board.dto.request.PostRequestDto;
import com.project.growfit.domain.board.dto.response.PostResponseDto;
import com.project.growfit.domain.board.entity.Image;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.domain.board.repository.ImageRepository;
import com.project.growfit.domain.board.repository.PostRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.s3.service.S3UploadService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class PostService {

    private final ParentRepository parentRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public Post savePost(PostRequestDto dto, List<MultipartFile> images) throws IOException {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = Post.createPost(dto, parent);
        postRepository.save(post);

        if (images != null && !images.isEmpty()) {
            int index = 0;
            for (MultipartFile image : images) {
                String imageUrl = s3UploadService.saveFile(image);
                Image imageEntity = Image.createImage(imageUrl, post, index++);
                imageRepository.save(imageEntity);
            }
        }
        return post;
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long boardId) {
        Post post = postRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        Parent parent = parentRepository.findById(post.getParent().getId()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return PostResponseDto.from(post, parent.getNickname());
    }

    @Transactional
    public int deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        int imageCnt = 0;

        for (Image image : post.getImageList()) {
            s3UploadService.deleteFile(image.getImageUrl());
            imageCnt++;
        }
        postRepository.delete(post);
        return imageCnt;
    }

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getEmail();
    }
}
