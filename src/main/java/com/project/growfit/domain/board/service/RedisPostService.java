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
import java.time.Duration;
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
public class RedisPostService {

    private final ParentRepository parentRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final StringRedisTemplate redisTemplate;

    public RedisPostService(
            ParentRepository parentRepository,
            PostRepository postRepository,
            LikeRepository likeRepository,
            @Qualifier("communityStatsRedisTemplate") StringRedisTemplate redisTemplate) {

        this.parentRepository = parentRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.redisTemplate = redisTemplate;
    }

    //private final static String PREFIX = "like:count:";
    private static final String LIKE_COUNT_PREFIX = "like:count:";
    private static final String COMMENT_COUNT_PREFIX = "comment:count:";
    private static final String POST_VIEW_PREFIX = "post:view:";

    @Transactional
    public boolean postLike(Long postId) {
        Parent parent = parentRepository.findByEmail(getCurrentEmail()).orElseThrow(() -> new BusinessException(
                ErrorCode.USER_NOT_FOUND));
        Optional<Like> existingLike = likeRepository.findByPostIdAndParentId(postId, parent.getId());

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            decrement(postId, LIKE_COUNT_PREFIX);  // redis 캐시 감소
            return false;
        } else {
            Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
            likeRepository.save(new Like(post, parent));
            increment(postId, LIKE_COUNT_PREFIX);  // redis 캐시 증가
            return true;
        }
    }

    private String getKey(String prefix, Long postId) {
        return prefix + postId;
    }

    private Long increment(Long postId, String prefix) {
        return redisTemplate.opsForValue().increment(getKey(prefix, postId));
    }

    private Long decrement(Long postId, String prefix) {
        return redisTemplate.opsForValue().decrement(getKey(prefix, postId));
    }

    public Integer getOrInit(Long postId, Supplier<Integer> dbCountSupplier, String prefix) {
        String key = getKey(prefix, postId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                log.warn("Redis에 저장된 값이 숫자가 아닙니다: {}", value);
            }
        }

        // 캐시에 없으면 DB에서 count 조회
        Integer countFromDb = dbCountSupplier.get();
        redisTemplate.opsForValue().set(key, String.valueOf(countFromDb));
        return countFromDb;
    }

    public void increaseHitIfNotViewed(Post post, Long parentId) {
        String key = "post:view:" + post.getId() + ":" + parentId;

        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofMinutes(30));
        if (Boolean.TRUE.equals(isNew)) {
            post.increaseHit();
        }
    }

    public void deletePostLikeCount(Long postId) {
        redisTemplate.delete(LIKE_COUNT_PREFIX + postId);
    }

    private String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return details.getEmail();
    }
}
