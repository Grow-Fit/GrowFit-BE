package com.project.growfit.domain.User.entity;

public enum ChildGender {
    MALE,
    FEMALE;

    public static ROLE toEnum(String role) {
        try {
            return ROLE.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
