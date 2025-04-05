package com.project.growfit.domain.Diet.repository;

import com.project.growfit.domain.Diet.entity.DailyDiet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyDietRepository extends JpaRepository<DailyDiet, Long> {
}
