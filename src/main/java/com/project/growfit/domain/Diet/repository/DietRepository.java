package com.project.growfit.domain.Diet.repository;

import com.project.growfit.domain.Diet.entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
}
