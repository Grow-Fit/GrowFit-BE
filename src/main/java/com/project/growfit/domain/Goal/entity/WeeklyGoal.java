package com.project.growfit.domain.Goal.entity;

import com.project.growfit.domain.Goal.dto.request.CreateWeeklyGoalRequestDto;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "weekly_goal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_goal_id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "certification_count", nullable = false)
    private int certificationCount;

    @Column(name = "is_letter_sent", nullable = false)
    private boolean isLetterSent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @OneToMany(mappedBy = "weeklyGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goal> goalList = new ArrayList<>();

    public static WeeklyGoal create(LocalDate startDate, LocalDate endDate, int certificationCount, Parent parent) {
        WeeklyGoal weeklyGoal = new WeeklyGoal();
        weeklyGoal.startDate = startDate;
        weeklyGoal.endDate = endDate;
        weeklyGoal.certificationCount = certificationCount;
        weeklyGoal.isLetterSent = false;
        weeklyGoal.parent = parent;
        return weeklyGoal;
    }

    public static WeeklyGoal create(LocalDate startDate, LocalDate endDate, int certificationCount, Parent parent, List<CreateWeeklyGoalRequestDto.GoalItem> goalItems) {
        WeeklyGoal weeklyGoal = new WeeklyGoal();
        weeklyGoal.startDate = startDate;
        weeklyGoal.endDate = endDate;
        weeklyGoal.certificationCount = certificationCount;
        weeklyGoal.isLetterSent = false;
        weeklyGoal.parent = parent;
        List<Goal> goals = goalItems.stream()
                .map(item -> Goal.create(item.name(), item.iconId(), weeklyGoal))
                .toList();
        weeklyGoal.goalList.addAll(goals);

        return weeklyGoal;
    }

    public void markLetterSent() {
        this.isLetterSent = true;
    }
}
