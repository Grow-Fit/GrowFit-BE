package com.project.growfit.domain.auth.dto.request;

import com.project.growfit.domain.auth.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;

public record ParentSignUpRequest(
        String email,
        String nickname,
        String id
) {
    public Parent toEntity(String email, String nickname, String id) {
        return new Parent(email, null, null, null, "kakao", id, ROLE.fromString("parent"));
    }
}