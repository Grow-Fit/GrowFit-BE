package com.project.growfit.domain.User.entity;

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
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "child_body_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildBodyInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "body_info_id")
    private Long id;

    @Column(name = "height", nullable = false)
    private long height;

    @Column(name = "weight", nullable = false)
    private long weight;

    @Column(name = "bmi")
    private long bmi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    public ChildBodyInfo(long height, long weight, Child child) {
        this.height = height;
        this.weight = weight;
        this.child = child;
    }

}
