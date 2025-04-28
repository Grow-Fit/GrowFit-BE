package com.project.growfit.domain.Diet.repository;

import com.project.growfit.domain.Diet.entity.CustomFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFoodRepository extends JpaRepository<CustomFood, Long> {
}
