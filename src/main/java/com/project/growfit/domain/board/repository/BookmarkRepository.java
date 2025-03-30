package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Bookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByPostIdAndParentId(Long postId, Long parentId);
    boolean existsByPostIdAndParentId(Long postId, Long parentId);
}
