package com.project.growfit.domain.Diet.repository;

import com.project.growfit.domain.Diet.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {


}
