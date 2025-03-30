package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByIdAndPostId(Long imageId, Long postId);

    @Query("SELECT COUNT(i) FROM Image i WHERE i.post.id = :postId")
    int countImagesByPostId(@Param("postId") Long postId);

    @Query("SELECT i.imageUrl FROM Image i WHERE i.post.id = :postId ORDER BY i.orderIndex ASC LIMIT 1")
    String findFirstImageUrlByPostId(@Param("postId") Long postId);
}
