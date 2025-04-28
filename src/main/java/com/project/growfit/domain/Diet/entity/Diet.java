package com.project.growfit.domain.Diet.entity;

import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.User.entity.Child;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "diet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_id")
    private Long id;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "meal_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_diet_id")
    private DailyDiet dailyDiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @Column(nullable = false)
    @JoinColumn(name = "total_calorie")
    private double totalCalorie;

    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Food> foodList = new ArrayList<>();

    public static Diet create(String time, MealType mealType, DailyDiet dailyDiet, Child child, @NotBlank(message = "음식을 입력해주세요.") List<Food> foodList) {
        Diet diet = new Diet();
        diet.time = LocalTime.parse(time);
        diet.mealType = mealType;
        diet.dailyDiet = dailyDiet;
        diet.child = child;
        for (Food food : foodList) {
            food.registerDiet(diet);
        }
        diet.foodList = foodList;
        diet.totalCalorie = calcCalorie(foodList);
        return diet;
    }

    public void addFood(Food food) {
        food.registerDiet(this);
        this.foodList.add(food);
    }

    public void edit(List<Food> foodList, UpdateDietRequestDto dto) {
        this.time = LocalTime.parse(dto.eatTime());
        for (Food food : this.foodList) food.registerDiet(null);
        this.foodList.clear();
        for (Food food : foodList) this.addFood(food);
        this.totalCalorie = calcCalorie(foodList);
    }

    public void registerDailyDiet(DailyDiet dailyDiet) {
        this.dailyDiet = dailyDiet;
    }

    private static double calcCalorie(List<Food> foodList) {
        double total = 0;
        for (Food food : foodList) total += food.getCalorie();
        return total;
    }
}
