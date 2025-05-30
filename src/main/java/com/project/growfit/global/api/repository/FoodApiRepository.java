package com.project.growfit.global.api.repository;

import com.project.growfit.global.api.entity.FoodApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodApiRepository extends JpaRepository<FoodApi, Long> {
    Page<FoodApi> findByFoodNmContaining(String keyword, PageRequest pageRequest);

}
