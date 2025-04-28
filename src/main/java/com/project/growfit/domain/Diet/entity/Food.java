package com.project.growfit.domain.Diet.entity;

import com.project.growfit.global.api.entity.FoodApi;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "food")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    @Column(name = "source_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodSourceType sourceType;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private double calorie;

    @Column(nullable = false)
    private double carbohydrate;

    @Column(nullable = false)
    private double fat;

    @Column(nullable = false)
    private double protein;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id")
    private Diet diet;

    public void registerDiet(Diet diet) {
        this.diet = diet;
    }

    public static Food fromFood(FoodApi api) {
        Food food = new Food();
        food.sourceType = FoodSourceType.API;
        food.name = api.getFoodNm();
        food.calorie = api.getEnerc();
        food.carbohydrate = api.getChocdf();
        food.fat = api.getFatce();
        food.protein = api.getProt();
        return food;
    }

    public static Food fromFood(CustomFood customFood) {
        Food food = new Food();
        food.name = customFood.getName();
        food.calorie = customFood.getCalorie();
        food.carbohydrate = customFood.getCarbohydrate();
        food.fat = customFood.getFat();
        food.protein = customFood.getProtein();
        food.sourceType = FoodSourceType.CUSTOM;
        return food;
    }

    public void addDiet(Diet diet) {
        this.diet = diet;
    }
}
