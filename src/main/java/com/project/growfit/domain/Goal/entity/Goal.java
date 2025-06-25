package com.project.growfit.domain.Goal.entity;

import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "goal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "icon_id", nullable = false)
    private int iconId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_goal_id", nullable = false)
    private WeeklyGoal weeklyGoal;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certificationList = new ArrayList<>();

    public static Goal create(String name, int iconId, WeeklyGoal weeklyGoal) {
        Goal goal = new Goal();
        goal.name = name;
        goal.iconId = iconId;
        goal.status = GoalStatus.PENDING;
        goal.weeklyGoal = weeklyGoal;
        return goal;
    }
    public void addCertification(Certification certification) {
        this.certificationList.add(certification);
        certification.assignToGoal(this); // 양방향 연관관계 설정
        updateStatusByCertificationCount();
    }

    private void updateStatusByCertificationCount() {
        int currentCount = this.certificationList.size();
        int requiredCount = this.weeklyGoal.getCertificationCount();
        if (currentCount == requiredCount) {
            this.status = GoalStatus.COMPLETE;
        } else {
            this.status = GoalStatus.PROGRESS;
        }
    }

    public void updateTitle(String title) {
        this.name = title;
    }
}
