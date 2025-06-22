package com.project.growfit.domain.User.service;

import com.project.growfit.domain.User.dto.request.AuthChildRequestDto;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ChildResponseDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthChildService {
    ChildResponseDto findByCode(String code);
    ChildInfoResponseDto registerChildCredentials(Long child_id, AuthChildRequestDto request);
    ChildResponseDto login(AuthChildRequestDto request, HttpServletResponse response);
    ChildResponseDto logout(HttpServletResponse response);
    ChildResponseDto findChildID(String code);
    ChildInfoResponseDto findChildPassword(FindChildPasswordRequestDto request);
}
