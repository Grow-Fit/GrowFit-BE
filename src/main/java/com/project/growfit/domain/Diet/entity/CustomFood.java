package com.project.growfit.domain.Diet.entity;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "custom_food")
@Getter
public class CustomFood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Parent createdBy;
    private String name;
    private double calorie;
    private double carbohydrate;
    private double fat;
    private double protein;

    public static CustomFood create(Parent parent, String name, double kcal, double carb, double fat, double protein) {
        CustomFood food = new CustomFood();
        food.createdBy = parent;
        food.name = name;
        food.calorie = kcal;
        food.carbohydrate = carb;
        food.fat = fat;
        food.protein = protein;
        return food;
    }
}
