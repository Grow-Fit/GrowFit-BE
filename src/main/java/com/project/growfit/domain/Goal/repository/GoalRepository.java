package com.project.growfit.domain.Goal.repository;

import com.project.growfit.domain.Goal.entity.Certification;
import com.project.growfit.domain.Goal.entity.Goal;
import com.project.growfit.domain.Goal.entity.WeeklyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByWeeklyGoal(WeeklyGoal weeklyGoal);
}
