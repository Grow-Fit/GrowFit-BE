package com.project.growfit.domain.Goal.repository;

import com.project.growfit.domain.Goal.entity.Certification;
import com.project.growfit.domain.Goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    boolean existsByGoalAndCertifiedAtBetween(Goal goal, LocalDateTime start, LocalDateTime end);
}
