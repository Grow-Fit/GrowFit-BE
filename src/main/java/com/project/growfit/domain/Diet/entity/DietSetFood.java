package com.project.growfit.domain.Diet.entity;

import com.project.growfit.domain.Diet.dto.request.FoodItemDto;
import com.project.growfit.domain.Diet.repository.CustomFoodRepository;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.api.entity.FoodApi;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.entity.BaseEntity;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diet_set_food")
public class DietSetFood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_set_food_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_set_id")
    private DietSet dietSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private FoodSourceType sourceType;

    @Column(name = "food_api_id")
    private Long foodApiId;

    @Column(name = "name")
    private String name;

    @Column(name = "calorie")
    private Double calorie;

    @Column(name = "carbohydrate")
    private Double carbohydrate;

    @Column(name = "fat")
    private Double fat;

    @Column(name = "protein")
    private Double protein;

    @Column(nullable = false)
    private int count = 1;

    public static DietSetFood create(FoodItemDto dto, FoodApiRepository foodApiRepository, CustomFoodRepository customFoodRepository, Parent parent) {
        if (dto.foodId() != null) {
            FoodApi api = foodApiRepository.findById(dto.foodId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FOOD_NOT_FOUND));
            DietSetFood food = new DietSetFood();
            food.sourceType = FoodSourceType.API;
            food.name = api.getFoodNm();
            food.calorie = api.getEnerc();
            food.carbohydrate = api.getChocdf();
            food.fat = api.getFatce();
            food.protein = api.getProt();
            food.count = dto.count();
            return food;
        } else {
            CustomFood custom = CustomFood.create(
                    parent,
                    dto.name(),
                    dto.kcal(),
                    dto.carbohydrate(),
                    dto.fat(),
                    dto.protein()
            );
            customFoodRepository.save(custom);
            DietSetFood food = new DietSetFood();
            food.sourceType = FoodSourceType.CUSTOM;
            food.name = custom.getName();
            food.calorie = custom.getCalorie();
            food.carbohydrate = custom.getCarbohydrate();
            food.fat = custom.getFat();
            food.protein = custom.getProtein();
            food.count = dto.count();
            return food;
        }
    }

    public void registerDietSet(DietSet dietSet) {
        this.dietSet = dietSet;
    }

    public Food toFood(FoodApi api, Diet diet) {
        if (this.sourceType == FoodSourceType.API) {
            return Food.fromFood(api, this.count);
        } else if (this.sourceType == FoodSourceType.CUSTOM) {
            return Food.create(
                    FoodSourceType.CUSTOM,
                    this.name,
                    this.calorie,
                    this.carbohydrate,
                    this.fat,
                    this.protein,
                    this.count,
                    diet);
        } else {
            throw new BusinessException(ErrorCode.INVALID_FOOD_SOURCE_TYPE);
        }
    }

}