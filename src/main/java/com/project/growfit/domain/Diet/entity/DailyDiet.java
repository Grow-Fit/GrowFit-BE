package com.project.growfit.domain.Diet.entity;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.global.entity.BaseEntity;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
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

    @Column(nullable = false)
    @JoinColumn(name = "total_calorie")
    private double totalCalorie;

    @OneToMany(mappedBy = "dailyDiet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diet> diets = new ArrayList<>();

    public DailyDiet(Child child, LocalDate date) {
        this.child = child;
        this.date = date;
    }
    public void addDiet(Diet diet) {
        if (hasMealType(diet.getMealType()))
            throw new BusinessException(ErrorCode.DIET_ALREADY_EXISTS);
        diet.registerDailyDiet(this);
        this.diets.add(diet);
        this.totalCalorie += diet.getTotalCalorie();
    }

    public void markSticker(Sticker sticker) {
        this.sticker = sticker;
    }

    private boolean hasMealType(MealType mealType){
        return this.diets.stream()
                .anyMatch(diet -> diet.getMealType() == mealType);
    }
}