package com.project.growfit.domain.Diet.repository;

import com.project.growfit.domain.Diet.entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
    @Query("SELECT d FROM Diet d JOIN FETCH d.foodList WHERE d.id = :dietId")
    Optional<Diet> findDietWithFoodList(@Param("dietId") Long dietId);

}
