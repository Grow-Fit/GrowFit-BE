package com.project.growfit.global.auto.service;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;

    public Object getAuthenticatedUser() {
        CustomUserDetails details = getCurrentUser();
        return switch (details.user_role()) {
            case "ROLE_PARENT" -> parentRepository.findByEmail(details.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            case "ROLE_CHILD" -> childRepository.findByLoginId(details.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            default -> throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        };
    }

    public CustomUserDetails getCurrentDetails() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) auth.getPrincipal();
    }
}