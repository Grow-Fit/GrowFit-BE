package com.project.growfit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(name = "child_id", unique = true)
    private String childId;

    @Column(name = "child_password")
    private String childPassword;

    @Column(name = "child_name")
    private String childName;

    @Column(name = "child_gender")
    private GENDER childGender;

    @Column(name = "child_age")
    private int childAge;

    @Column(name = "child_height")
    private long childHeight;

    @Column(name = "child_weight")
    private long childWeight;

    @Column(name = "child_nickname", unique = true)
    private String childNickname;

    @Column(name = "child_code", unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    public Child(String childId,
                 String childNickname,
                 GENDER gender,
                 int childAge,
                 long childHeight,
                 long childWeight,
                 String childPassword,
                 ROLE role) {
        this. childId = childId;
        this.childNickname = childNickname;
        this.childAge = childAge;
        this.childGender = gender;
        this.childHeight = childHeight;
        this.childWeight = childWeight;
        this.childPassword = childPassword;
        this.role = role;
    }

    public Child(String childId, String childPassword, String childName, GENDER childGender, int childAge, int childHeight, int child_weight, String childNickname, ROLE role) {
        this.childId = childId;
        this.code = null;
        this.childPassword = childPassword;
        this.childName = childName;
        this.childGender = childGender;
        this.childAge = childAge;
        this.childHeight = childHeight;
        this.childWeight = child_weight;
        this.childNickname = childNickname;
        this.role = role;
    }

    public Child(String childId, String childNickname, String childPassword, ROLE role) {
        this.childId = childId;
        this.childNickname = childNickname;
        this.childPassword = childPassword;
        this.role = role;
    }

    public String userId() {
        return childId;
    }

    public String userNickname(){return childNickname;}

    public ROLE user_role() {
        return role;
    }

    public String user_password() {
        return childPassword;
    }

    public void addRegister(Parent parent) {this.parent = parent;}
    public void updateCode(String code) {this.code = code;}
    public void updateNickname(String nickname){this.childNickname = nickname;}
    public void updatePassword(String password){
        this.childPassword = password;}
    public void updateCredentials(String id, String password) {
        this.childId = id;
        this.childPassword = password;
    }
}