package com.project.growfit.domain.User.repository;

import com.project.growfit.domain.User.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    Optional<Child> findByLoginId(String login_id);
    Optional<Child> findById(Long id);
    Optional<Child> findByCodeNumber(String code);
    boolean existsByCodeNumberAndLoginId(String code, String login_id);
    boolean existsByCodeNumber(String code);
    boolean existsByLoginIdOrPassword(String login_id, String password);
}