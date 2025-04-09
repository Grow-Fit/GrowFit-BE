package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt")
    List<Comment> findCommentsByPostId(@Param("postId") Long postId);

    int countByPostId(Long postId);
}
