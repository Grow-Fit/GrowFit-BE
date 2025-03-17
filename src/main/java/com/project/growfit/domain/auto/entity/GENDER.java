package com.project.growfit.domain.auto.entity;

public enum GENDER {
    GENDER_MALE,
    GENDER_FEMALE;

    public static ROLE toEnum(String role) {
        try {
            return ROLE.valueOf("GENDER" + role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
