package com.project.growfit.domain.Diet.entity;

import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "image")
    private String imageUrl;

    @Column(name = "meal_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_diet_id")
    private DailyDiet dailyDiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Food> foodList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private DietState state;

    @Column(nullable = false)
    private double totalCalorie;

    @Column(nullable = false)
    private double totalCarbohydrate;

    @Column(nullable = false)
    private double totalProtein;

    @Column(nullable = false)
    private double totalFat;

    public static Diet create(LocalTime time, MealType mealType, DailyDiet dailyDiet, Child child, @NotBlank(message = "음식을 입력해주세요.") List<Food> foodList) {
        Diet diet = new Diet();
        diet.time = time;
        diet.mealType = mealType;
        diet.dailyDiet = dailyDiet;
        diet.child = child;
        for (Food food : foodList) food.registerDiet(diet);
        diet.totalCalorie = calcCalorie(foodList);
        diet.totalCarbohydrate = calcCarbohydrate(foodList);
        diet.totalFat = calcFat(foodList);
        diet.totalProtein = calcProtein(foodList);
        diet.foodList = foodList;
        diet.state = DietState.NONE;
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
        recalculate(foodList);
    }

    public void edit(List<Food> foodList) {
        for (Food food : this.foodList) food.registerDiet(null);
        this.foodList.clear();

        for (Food food : foodList) this.addFood(food);
        recalculate(foodList);
    }

    public void updateState(DietState newState) {
        this.state = newState;
    }

    public void registerDailyDiet(DailyDiet dailyDiet) {
        this.dailyDiet = dailyDiet;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateTime(LocalTime newTime) {
        this.time = newTime;
    }

    private static double calcCalorie(List<Food> foodList) {
        return foodList.stream()
                .mapToDouble(f -> f.getCalorie() * f.getCount())
                .sum();
    }

    private static double calcCarbohydrate(List<Food> foodList) {
        return foodList.stream()
                .mapToDouble(f -> f.getCarbohydrate() * f.getCount())
                .sum();
    }

    private static double calcFat(List<Food> foodList) {
        return foodList.stream()
                .mapToDouble(f -> f.getFat() * f.getCount())
                .sum();
    }

    private static double calcProtein(List<Food> foodList) {
        return foodList.stream()
                .mapToDouble(f -> f.getProtein() * f.getCount())
                .sum();
    }

    private void recalculate(List<Food> foodList) {
        this.totalCalorie = calcCalorie(foodList);
        this.totalCarbohydrate = calcCarbohydrate(foodList);
        this.totalFat = calcFat(foodList);
        this.totalProtein = calcProtein(foodList);
    }

}
