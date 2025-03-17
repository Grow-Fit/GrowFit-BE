package com.project.growfit.domain.auto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;         // 이메일 (카카오 제공)
    private String name;          // 부모 이름
    private String profileImage;  // 프로필 이미지 (카카오 제공)
    private String phoneNumber;   // 부모 연락처
    private String provider;      // 로그인 제공자 (kakao)
    private String providerId;    // 카카오에서 제공하는 고유 ID

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Child> childs = new ArrayList<>();

    public Parent(String email, String name, String profileImage, String phoneNumber, String provider, ROLE role) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    public Parent(String email, String name, String profileImage, String phoneNumber, String provider, String providerId, ROLE role) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.provider = provider;
        this.providerId = providerId;
        this.role = ROLE.ROLE_PARENT; // 기본값 설정
    }

    public void updateNickname(String nickname){
        this.name = nickname;
    }

    public void addChild(Child child) {
        this.childs.add(child);
    }
}