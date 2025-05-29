package com.project.growfit.domain.notice.repository;

import com.project.growfit.domain.notice.entity.Notice;
import com.project.growfit.domain.notice.entity.TargetType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findByIdAndTargetTypeAndTargetId(Long id, TargetType targetType, Long targetId);
}
