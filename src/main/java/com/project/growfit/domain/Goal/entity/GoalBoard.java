package com.project.growfit.domain.Goal.entity;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "goal_board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_board_id")
    private Long id;

    @OneToOne(mappedBy = "goalBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Child child;

    @OneToMany(mappedBy = "goalBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoalType> goalTypeList = new ArrayList<>();
}
