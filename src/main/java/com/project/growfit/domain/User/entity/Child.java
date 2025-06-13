package com.project.growfit.domain.User.entity;

import com.project.growfit.domain.Diet.entity.Diet;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "child")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Child extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login_id", length = 100)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChildGender gender;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "code_number", length = 100)
    private String codeNumber;

    @Column(name = "nickname", length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildBodyInfo> bodyInfoList = new ArrayList<>();

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diet> dietList = new ArrayList<>();

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BmiAnalysis> analysisList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private ChildCharacter childCharacter ;

    public Child(String childId, String name, ChildGender gender, int childAge, long childHeight, long childWeight, String childPassword, ROLE role) {
        this.loginId = childId;
        this.name = name;
        this.age = childAge;
        this.gender = gender;
        bodyInfoList.add(new ChildBodyInfo(childHeight, childWeight, this));
        this.password = childPassword;
        this.role = role;
    }

    public Child(String childId, String childPassword, String childName, ChildGender childGender, int childAge, int childHeight, int childWeight, String childNickname, ROLE role) {
        this.loginId = childId;
        this.codeNumber = null;
        this.password = childPassword;
        this.name = childName;
        this.gender = childGender;
        this.age = childAge;
        bodyInfoList.add(new ChildBodyInfo(childHeight, childWeight, this));
        this.nickname = childNickname;
        this.role = role;
    }

    public Child(String childId, String childNickname, String childPassword, ROLE role) {
        this.loginId = childId;
        this.password = childPassword;
        this.nickname = childNickname;
        this.role = role;
    }

    public String userId() {
        return loginId;
    }

    public String userNickname(){return nickname;}

    public ROLE user_role() {
        return role;
    }

    public String user_password() {
        return password;
    }

    public void addRegister(Parent parent) {this.parent = parent;}
    public void updateCode(String code) {this.codeNumber = code;}
    public void updateNickname(String nickname){this.nickname = nickname;}
    public void updatePassword(String password){
        this.password = password;}

    public void updateCredentials(String id, String password, String nickname) {
        this.loginId = id;
        this.password = password;
        this.nickname = nickname;
    }

    public ChildBodyInfo getLatestBodyInfo() {
        return bodyInfoList.isEmpty() ? null : bodyInfoList.get(0);
    }

}
