package com.project.growfit.domain.User.dto.request;

import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;

public record ParentSignUpRequest(
        String email,
        String nickname,
        String id
) {
    public Parent toEntity(String email, String nickname, String id) {
        return new Parent(email, nickname, null, "kakao", id, ROLE.fromString("parent"));
    }
}