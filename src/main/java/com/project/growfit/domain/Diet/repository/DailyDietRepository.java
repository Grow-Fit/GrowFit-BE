package com.project.growfit.domain.Diet.repository;

import com.project.growfit.domain.Diet.entity.DailyDiet;
import com.project.growfit.domain.User.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyDietRepository extends JpaRepository<DailyDiet, Long> {
    Optional<DailyDiet> findByChildAndDate(Child child, LocalDate date);

    List<DailyDiet> findByChild(Child child);

    List<DailyDiet> findAllByChildAndDateBetween(Child child, LocalDate startDate, LocalDate endDate);
}
