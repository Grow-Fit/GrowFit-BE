package com.project.growfit.global.api.repository;

import com.project.growfit.global.api.entity.FoodApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodApiRepository extends JpaRepository<FoodApi, Long> {
}
