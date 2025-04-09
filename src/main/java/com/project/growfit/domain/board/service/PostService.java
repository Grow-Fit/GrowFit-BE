package com.project.growfit.domain.board.service;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.board.dto.request.PostRequestDto;
import com.project.growfit.domain.board.dto.response.CustomPageResponse;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final RedisPostService redisPostService;

    private static final String LIKE_COUNT_PREFIX = "like:count:";
    private static final String COMMENT_COUNT_PREFIX = "comment:count:";
    private static final String POST_VIEW_PREFIX = "post:view:";

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

    @Transactional
    public PostResponseDto getPost(Long boardId) {
        Post post = postRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        redisPostService.increaseHitIfNotViewed(post, parent.getId());
        int likeCount = redisPostService.getOrInit(boardId, () -> likeRepository.countByPostId(boardId), LIKE_COUNT_PREFIX);  // redis에서 조회
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
        redisPostService.deletePostLikeCount(postId);
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

    public CustomPageResponse<PostResponseDto> getPosts(Category category, List<Age> ages, String sort, int page, int size) {
        Specification<Post> spec = Specification.where(PostSpecification.hasCategory(category))
                .and(PostSpecification.hasAnyAge(ages));

        boolean isLikeSort = "like".equals(sort);
        Pageable pageable = PageRequest.of(page, size);
        Parent parent = findCurrentParent();

        if (!isLikeSort) {
            spec = spec.and(getSortSpec(sort));
            Page<Post> postPage = postRepository.findAll(spec, pageable);
            List<PostResponseDto> postDtos = toPostDtos(postPage.getContent(), parent);
            return CustomPageResponse.from(new PageImpl<>(postDtos, pageable, postPage.getTotalElements()));
        }

        // 좋아요 정렬
        List<Post> posts = postRepository.findAll(spec);
        List<PostResponseDto> postDtos = toPostDtos(posts, parent);

        return sortAndPaginateByLike(postDtos, page, size);
    }

    private Specification<Post> getSortSpec(String sort) {
        return switch (sort) {
            case "hits" -> PostSpecification.orderByHits();
            default -> PostSpecification.orderByCreatedAt();
        };
    }

    private Parent findCurrentParent() {
        return parentRepository.findByEmail(getCurrentEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private List<PostResponseDto> toPostDtos(List<Post> posts, Parent parent) {
        return posts.stream()
                .map(post -> {
                    boolean isLike = likeRepository.existsByPostIdAndParentId(post.getId(), parent.getId());
                    boolean isBookmark = bookmarkRepository.existsByPostIdAndParentId(post.getId(), parent.getId());
                    int likeCount = redisPostService.getOrInit(post.getId(),
                            () -> likeRepository.countByPostId(post.getId()), LIKE_COUNT_PREFIX);
                    return PostResponseDto.from(post, post.getParent().getNickname(), likeCount, isLike, isBookmark);
                })
                .collect(Collectors.toList());
    }

    private CustomPageResponse<PostResponseDto> sortAndPaginateByLike(List<PostResponseDto> dtos, int page, int size) {
        dtos.sort(Comparator.comparingInt(PostResponseDto::likeCount).reversed());

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, dtos.size());

        if (fromIndex >= dtos.size()) {
            return CustomPageResponse.from(new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), dtos.size()));
        }

        List<PostResponseDto> paged = new ArrayList<>(dtos.subList(fromIndex, toIndex));
        return CustomPageResponse.from(new PageImpl<>(paged, PageRequest.of(page, size), dtos.size()));
    }
}
