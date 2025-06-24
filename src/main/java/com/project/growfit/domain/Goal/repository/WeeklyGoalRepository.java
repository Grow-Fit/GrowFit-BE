package com.project.growfit.domain.Goal.repository;

import com.project.growfit.domain.Goal.entity.WeeklyGoal;
import com.project.growfit.domain.User.entity.Parent;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyGoalRepository extends JpaRepository<WeeklyGoal, Long> {
    // 주어진 start~end 날짜 범위와 겹치는 기존 주간 목표가 해당 parent에게 존재하는지 검사합니다.
    boolean existsByParentAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Parent parent, LocalDate start, LocalDate end);
    // 해당 날짜(date)가 포함된 주간 목표가 parent에게 존재하는지 단일 주간 목표를 찾는 메서드입니다.
    Optional<WeeklyGoal> findByParentAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Parent parent, LocalDate startDate, LocalDate endDate);
    //
}
