package com.project.growfit.domain.board.service;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.board.dto.request.PostRequestDto;
import com.project.growfit.domain.board.dto.response.PostResponseDto;
import com.project.growfit.domain.board.entity.Age;
import com.project.growfit.domain.board.entity.Bookmark;
import com.project.growfit.domain.board.entity.Category;
import com.project.growfit.domain.board.entity.Image;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.domain.board.repository.BookmarkRepository;
import com.project.growfit.domain.board.repository.ImageRepository;
import com.project.growfit.domain.board.repository.LikeRepository;
import com.project.growfit.domain.board.repository.PostRepository;
import com.project.growfit.domain.board.repository.PostSpecification;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.s3.service.S3UploadService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final S3UploadService s3UploadService;
    private final PostLikeService postLikeService;

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
        int likeCount = postLikeService.getOrInit(boardId, () -> likeRepository.countByPostId(boardId));  // redis에서 조회
        boolean isLike = likeRepository.existsByPostIdAndParentId(post.getId(), parent.getId());
        boolean isBookmark = bookmarkRepository.existsByPostIdAndParentId(post.getId(), parent.getId());
        return PostResponseDto.from(post, parent.getNickname(), likeCount, isLike, isBookmark);
    }

    @Transactional
    public int deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        checkPostOwnership(post);

        int imageCnt = 0;
        for (Image image : post.getImageList()) {
            s3UploadService.deleteFile(image.getImageUrl());
            imageCnt++;
        }
        postRepository.delete(post);
        return imageCnt;
    }

    @Transactional
    public Post updatePost(PostRequestDto dto, List<MultipartFile> images, Long postId) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        checkPostOwnership(post);

        post.updatePostContent(dto);
        handleDeleteImages(post, imageIds(post));
        
        if (images != null && !images.isEmpty()) {
            handleImageUpdate(post, images);
        }
        return post;
    }

    private void handleImageUpdate(Post post, List<MultipartFile> images) throws IOException {
        int currentOrderIndex = post.getImageList().size();

        for (MultipartFile image : images) {
            String imageUrl = s3UploadService.saveFile(image);
            Image newImage = Image.createImage(imageUrl, post, currentOrderIndex++);
            imageRepository.save(newImage);
        }
    }

    private void handleDeleteImages(Post post, List<Long> deletedImageIds) {
        if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
            for (Long imageId : deletedImageIds) {
                Image image = imageRepository.findByIdAndPostId(imageId, post.getId()).orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
                s3UploadService.deleteFile(image.getImageUrl());
                post.getImageList().remove(image);
                imageRepository.delete(image);
            }
        }
    }

    private static List<Long> imageIds(Post post) {
        return post.getImageList().stream()
                .map(Image::getId)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean bookmarkPost(Long postId) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByPostIdAndParentId(postId, parent.getId());

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
            return false;
        } else {
            Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
            bookmarkRepository.save(new Bookmark(post, parent));
            return true;
        }
    }

    private void checkPostOwnership(Post post) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (!post.getParent().getEmail().equals(parent.getEmail())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getEmail();
    }

    public List<PostResponseDto> getPosts(Category category, List<Age> ages, String sort) {
        Specification<Post> spec = Specification.where(PostSpecification.hasCategory(category)).and(PostSpecification.hasAnyAge(ages));

        if ("hits".equals(sort)) {
            spec = spec.and(PostSpecification.orderByHits());
        } else if ("like".equals(sort)) {
            spec = spec.and(PostSpecification.orderByLikes());
        } else{
            spec = spec.and(PostSpecification.orderByCreatedAt());
        }

        List<Post> posts = postRepository.findAll(spec);

        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return posts.stream()
                .map(post -> {
                    boolean isLike = likeRepository.existsByPostIdAndParentId(post.getId(), parent.getId());
                    boolean isBookmark = bookmarkRepository.existsByPostIdAndParentId(post.getId(), parent.getId());
                    int likeCount = postLikeService.getOrInit(post.getId(), () -> likeRepository.countByPostId(post.getId()));  // redis에서 조회
                    return PostResponseDto.from(post, post.getParent().getNickname(), likeCount, isLike, isBookmark);
                }).collect(Collectors.toList());
    }
}
