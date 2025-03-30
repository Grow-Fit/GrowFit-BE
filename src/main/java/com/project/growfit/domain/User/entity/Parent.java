package com.project.growfit.domain.User.entity;

import com.project.growfit.domain.board.entity.Bookmark;
import com.project.growfit.domain.board.entity.Comment;
import com.project.growfit.domain.board.entity.Like;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "parent")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Parent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "introduce", columnDefinition = "TEXT")
    private String introduce;

    @Column(name = "photo")
    private String photo;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    public Parent(String email, String name, String profileImage, String provider, ROLE role) {
        this.email = email;
        this.nickname = name;
        this.photo = profileImage;
        this.provider = provider;
        this.role = role;
    }

    public Parent(String email, String name, String profileImage, String provider, String providerId, ROLE role) {
        this.email = email;
        this.nickname = name;
        this.photo = profileImage;
        this.provider = provider;
        this.providerId = providerId;
        this.role = ROLE.ROLE_PARENT;
    }

    public boolean hasChildWithName(String childName) {
        return children.stream()
                .anyMatch(child -> child.getName().equals(childName));
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void addChild(Child child) {
        this.children.add(child);
    }
}
