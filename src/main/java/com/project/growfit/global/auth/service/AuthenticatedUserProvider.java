package com.project.growfit.global.auth.service;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    public Parent getAuthenticatedParent() {
        CustomUserDetails user = getCurrentUser();
        validateRole(user, "ROLE_PARENT");
        return getParentByEmail(user.getEmail());
    }


    public Child getAuthenticatedChild() {
        CustomUserDetails user = getCurrentUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_CHILD); // 인증되지 않은 사용자
        }
        return switch (user.getRole()) {
            case "ROLE_CHILD" -> getChildByLoginId(user.getUserId());
            case "ROLE_PARENT" -> {
                Parent parent = getParentByEmail(user.getEmail());
                yield getChildByParent(parent);
            }
            default -> throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        };
    }

    public Child getAuthenticatedChildForRegistration() {
        CustomUserDetails user = getCurrentUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_CHILD); // 인증되지 않은 사용자
        }
        return switch (user.getRole()) {
            case "ROLE_CHILD" -> getChildByLoginId(user.getUserId());
            case "ROLE_PARENT" -> {
                Parent parent = getParentByEmail(user.getEmail());
                List<Child> children = childRepository.findAllByParentOrderByCreatedAtDesc(parent);

                if (children.isEmpty()) {
                    throw new BusinessException(ErrorCode.CHILD_NOT_FOUND);
                }
                yield children.get(0);  // 가장 최근에 생성된 자녀
            }
            default -> throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        };
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        Object principal = auth.getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        return (CustomUserDetails) auth.getPrincipal();
    }

    private void validateRole(CustomUserDetails user, String requiredRole) {
        if (!user.getRole().equals(requiredRole)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private Parent getParentByEmail(String email) {
        return parentRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Child getChildByLoginId(String loginId) {
        return childRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }

    private Child getChildByParent(Parent parent) {
        return childRepository.findByParent(parent)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }
}