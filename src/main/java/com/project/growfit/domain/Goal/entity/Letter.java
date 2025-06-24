package com.project.growfit.domain.Goal.entity;

import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "letter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_goal_id", nullable = false)
    private WeeklyGoal weeklyGoal;

    public static Letter create(String content, WeeklyGoal weeklyGoal) {
        Letter letter = new Letter();
        letter.content = content;
        letter.weeklyGoal = weeklyGoal;
        return letter;
    }
}