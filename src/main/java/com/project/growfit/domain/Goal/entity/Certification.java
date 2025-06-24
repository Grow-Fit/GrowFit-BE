package com.project.growfit.domain.Goal.entity;

import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cert_goal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "certified_at", nullable = false)
    private LocalDateTime certifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    public static Certification create(String imageUrl, Goal goal) {
        LocalDate now = LocalDate.now();
        checkDateValidity(goal.getCertificationList(), now);

        Certification certification = new Certification();
        certification.imageUrl = imageUrl;
        certification.certifiedAt = LocalDateTime.now();
        certification.goal = goal;
        return certification;
    }

    public void assignToGoal(Goal goal) {

        this.goal = goal;
    }

    private static void checkDateValidity(List<Certification> certifications, LocalDate date) {
        boolean exists = certifications.stream()
                .anyMatch(cert -> cert.getCertifiedAt().toLocalDate().isEqual(date));
        if (exists) throw new BusinessException(ErrorCode.ALREADY_CERTIFIED_TODAY);
    }

}
