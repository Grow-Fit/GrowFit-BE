package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
