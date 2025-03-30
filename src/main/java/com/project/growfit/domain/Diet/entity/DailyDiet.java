package com.project.growfit.domain.Diet.entity;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_diet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DailyDiet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_diet_id")
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "sticker")
    private Sticker sticker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @OneToMany(mappedBy = "dailyDiet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diet> diets = new ArrayList<>();
}