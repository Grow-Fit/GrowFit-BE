package com.project.growfit.domain.Diet.entity;

import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "diet_set")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietSet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_set_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @Column(name = "set_name", nullable = false)
    private String setName;

    @Column(nullable = false)
    @JoinColumn(name = "total_calorie")
    private double totalCalorie;

    @OneToMany(mappedBy = "dietSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietSetFood> foods = new ArrayList<>();

    public static DietSet create(SaveDietSetRequestDto dto, Parent parent, List<DietSetFood> foods) {
        DietSet set = new DietSet();
        set.setName = dto.setName();
        set.parent = parent;
        set.totalCalorie = calcCalorie(foods);
        for (DietSetFood food : foods) set.addFood(food);
        return set;
    }

    public void addFood(DietSetFood food) {
        foods.add(food);
        food.registerDietSet(this);
    }

    private static double calcCalorie(List<DietSetFood> foodList) {
        double total = 0;
        for (DietSetFood food : foodList) total += food.getCalorie();
        return total;
    }

    public void update(String newName, List<DietSetFood> newFoods) {
        this.setName = newName;
        this.foods.clear();
        this.foods.addAll(newFoods);
        for (DietSetFood food : newFoods) {
            food.registerDietSet(this);
        }
        recalculateTotalCalories();
    }

    public void clearFoods() {
        this.foods.clear();
    }

    private void recalculateTotalCalories() {
        this.totalCalorie = foods.stream()
                .mapToDouble(DietSetFood::getCalorie)
                .sum();
    }
}
