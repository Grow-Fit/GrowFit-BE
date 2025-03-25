package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
