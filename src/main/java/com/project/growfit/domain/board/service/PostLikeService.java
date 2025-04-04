package com.project.growfit.domain.board.service;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.domain.board.entity.Like;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.domain.board.repository.LikeRepository;
import com.project.growfit.domain.board.repository.PostRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PostLikeService {

    private final ParentRepository parentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final StringRedisTemplate redisTemplate;

    public PostLikeService(
            ParentRepository parentRepository,
            PostRepository postRepository,
            LikeRepository likeRepository,
            @Qualifier("likeCountRedisTemplate") StringRedisTemplate redisTemplate
    ) {
        this.parentRepository = parentRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.redisTemplate = redisTemplate;
    }

    private final static String PREFIX = "like:count:";

    @Transactional
    public boolean postLike(Long postId) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(
                ErrorCode.USER_NOT_FOUND));
        Optional<Like> existingLike = likeRepository.findByPostIdAndParentId(postId, parent.getId());

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            decrement(postId);  // redis 캐시 감소
            return false;
        } else {
            Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
            likeRepository.save(new Like(post, parent));
            increment(postId);  // redis 캐시 증가
            return true;
        }
    }

    private String getKey(Long postId) {
        return PREFIX + postId;
    }

    private Long increment(Long postId) {
        return redisTemplate.opsForValue().increment(getKey(postId));
    }

    private Long decrement(Long postId) {
        return redisTemplate.opsForValue().decrement(getKey(postId));
    }

    public Integer getOrInit(Long postId, Supplier<Integer> dbCountSupplier) {
        String key = getKey(postId);
        String value = redisTemplate.opsForValue().get(key);

        if (value != null) return Integer.parseInt(value);

        // 캐시에 없으면 DB에서 count 조회
        Integer countFromDb = dbCountSupplier.get();
        redisTemplate.opsForValue().set(key, String.valueOf(countFromDb));
        return countFromDb;
    }

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getEmail();
    }
}
