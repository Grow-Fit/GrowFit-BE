package com.project.growfit.domain.User.service;

import com.project.growfit.domain.User.dto.request.AuthChildRequestDto;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthChildService {
    ResultResponse<?> findByCode(String code);
    ResultResponse<?> registerChildCredentials(Long child_id, AuthChildRequestDto request);
    ResultResponse<?> login(AuthChildRequestDto request, HttpServletResponse response);
    ResultResponse<String> logout(HttpServletResponse response);
    ResultResponse<?> findChildID(String code);
    ResultResponse<?> findChildPassword(FindChildPasswordRequestDto request);
}
