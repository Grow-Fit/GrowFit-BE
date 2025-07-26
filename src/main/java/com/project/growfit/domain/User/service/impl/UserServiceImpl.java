package com.project.growfit.domain.User.service.impl;

import com.project.growfit.domain.User.dto.request.ChildInfoRequestDto;
import com.project.growfit.domain.User.dto.request.ParentInfoRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ParentInfoResponseDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.ChildGender;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.service.UserService;
import com.project.growfit.global.auth.cookie.CookieService;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticatedUserProvider userProvider;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public ParentInfoResponseDto getParentInfo() {
        Parent parent = userProvider.getAuthenticatedParent();
        return ParentInfoResponseDto.toDto(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public ChildInfoResponseDto getChildInfo() {
        Child child = userProvider.getAuthenticatedChild();
        return ChildInfoResponseDto.toDto(child);
    }


    @Override
    @Transactional
    public void updateParentInfo(ParentInfoRequestDto request, HttpServletResponse response) {
        Parent parent = userProvider.getAuthenticatedParent();

        boolean isEmailChanged = !parent.getEmail().equals(request.email());
        String oldEmail = parent.getEmail();
        parent.updateNickname(request.nickname());

        if (isEmailChanged) parent.updateEmail(request.email());

        Child child = parent.getChildren().get(0);
        child.updateInfo(request.childAge(), request.childGender(), request.childName(), null, request.childHeight(), request.childWeight());

        if (isEmailChanged) jwtProvider.regenerateToken(oldEmail, request.email(), String.valueOf(parent.getRole()), "SOCIAL_KAKAO", response);
    }

    @Override
    @Transactional
    public void updateChildInfo(ChildInfoRequestDto request) {
        Child child = userProvider.getAuthenticatedChild();
        if (request.password() != null &&
                !request.password().isBlank() &&
                !passwordEncoder.matches(request.password(), child.getPassword())) {

            String encodedPassword = passwordEncoder.encode(request.password());
            child.updatePassword(encodedPassword);
        }
        child.updateInfo(request.age(), request.gender(), null, request.nickname(), request.height(), request.weight());
    }
}
