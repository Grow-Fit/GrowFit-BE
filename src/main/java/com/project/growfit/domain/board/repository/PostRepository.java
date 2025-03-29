package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.id IN (SELECT b.post.id FROM Bookmark b WHERE b.parent.id = :userId)")
    List<Post> findBookmarkPostsByUserId(@Param("userId") Long userId);
}
