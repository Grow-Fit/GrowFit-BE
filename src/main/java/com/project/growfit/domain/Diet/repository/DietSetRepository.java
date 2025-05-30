package com.project.growfit.domain.Diet.repository;

import com.project.growfit.domain.Diet.entity.DietSet;
import com.project.growfit.domain.User.entity.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietSetRepository extends JpaRepository<DietSet, Long> {
    Page<DietSet> findByParent(Parent parent, Pageable pageable);
}
