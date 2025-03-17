package com.project.growfit.domain.auto.repository;

import com.project.growfit.domain.auto.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    Optional<Child> findByChildId(String child_id);

    boolean existsByChildId(String child_id);
}