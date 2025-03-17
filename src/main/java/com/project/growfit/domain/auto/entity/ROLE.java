package com.project.growfit.domain.auto.entity;

public enum ROLE {
    ROLE_ADMIN,
    ROLE_CHILD,
    ROLE_PARENT;

    public static ROLE fromString(String role) {
        try {
            return ROLE.valueOf("ROLE_" + role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ROLE_CHILD; // 기본값: USER
        }
    }
}
