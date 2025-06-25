package com.project.growfit.domain.Goal.repository;

import com.project.growfit.domain.Goal.entity.Letter;
import com.project.growfit.domain.Goal.entity.WeeklyGoal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {
    Optional<Letter> findByWeeklyGoal(WeeklyGoal weeklyGoal);
    Page<Letter> findAllByWeeklyGoal_Parent_Id(Long parentId, Pageable pageable);
}