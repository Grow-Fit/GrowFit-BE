package com.project.growfit.domain.Goal.entity;

import com.project.growfit.domain.Diet.entity.Sticker;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "goal_progress")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_progress_id")
    private Long id;

    @Column(name = "progress_date", nullable = false)
    private LocalDateTime progressDate;

    @Column(name = "success_status", nullable = false)
    private SuccessStatus successStatus;

    @Column(name = "sticker")
    private Sticker sticker;

    @Column(name = "photo")
    private String photo;

    @Column(name = "weight")
    private long weight;
}
