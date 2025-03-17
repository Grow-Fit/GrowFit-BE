package com.project.growfit.domain.auto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
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

    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    public Child(String childId, String childNickname,
                 ROLE anEnum, int childAge,
                 long childHeight,
                 long childWeight,
                 String childPassword,
                 String role) {
        this. childId = childId;
        this.childNickname = childNickname;
        this.childAge = childAge;
        this.childHeight = childHeight;
        this.childWeight = childWeight;
        this.childPassword = childPassword;
        this.role = role;
    }

    public Child(String childId, String childPassword, String childName, GENDER childGender, int childAge, int childHeight, int child_weight, String childNickname, String role) {
        this.childId = childId;
        this.childPassword = childPassword;
        this.childName = childName;
        this.childGender = childGender;
        this.childAge = childAge;
        this.childHeight = childHeight;
        this.childWeight = child_weight;
        this.childNickname = childNickname;
        this.role = role;
    }

    public Child(String childId, String childNickname, String childPassword, String role) {
        this.childId = childId;
        this.childNickname = childNickname;
        this.childPassword = childPassword;
        this.role = role;
    }

    public String userId() {
        return childId;
    }

    public String userNickname(){return childNickname;}

    public String user_role() {
        return role;
    }

    public String user_password() {
        return childPassword;
    }

    public void addRegister(Parent parent) {this.parent = parent;}
}