package com.project.growfit.domain.Diet.entity;

import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String mainCategory;

    @Column(nullable = false, length = 30)
    private String subCategory;

    @Column(nullable = false, length = 30)
    private String manufacturer;

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

}
