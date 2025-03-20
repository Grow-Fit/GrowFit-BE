package com.project.growfit.domain.auth.entity;

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
    private String email;
    private String name;
    private String profileImage;
    private String phoneNumber;
    private String provider;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Child> children = new ArrayList<>();

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
        this.role = ROLE.ROLE_PARENT;
    }

    public boolean hasChildWithName(String childName) {
        return children.stream()
                .anyMatch(child -> child.getChildName().equals(childName));
    }

    public void updateNickname(String nickname){
        this.name = nickname;
    }

    public void addChild(Child child) {
        this.children.add(child);
    }
}