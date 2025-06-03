package com.project.growfit.domain.User.repository;

import com.project.growfit.domain.User.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByEmail(String email);
    Optional<List<Parent>> findParentByProviderId(String kakaoIdentifier);

    Optional<Parent> findBySocialId(String socialId);
}
