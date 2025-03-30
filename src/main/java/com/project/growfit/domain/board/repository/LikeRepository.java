package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostIdAndParentId(Long postId, Long parentId);
    int countByPostId(Long postId);
    boolean existsByPostIdAndParentId(Long postId, Long parentId);
}
