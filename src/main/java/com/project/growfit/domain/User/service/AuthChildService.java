package com.project.growfit.domain.User.service;

import com.project.growfit.domain.User.dto.request.ChildCredentialsRequest;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthChildService {
    ResultResponse<?> findByCode(String code);
    ResultResponse<?> registerChildCredentials(Long child_id, ChildCredentialsRequest request);
    ResultResponse<?> login(ChildCredentialsRequest request, HttpServletResponse response);
    ResultResponse<?> updateNickname(Long child_id, UpdateNicknameRequestDto request);
    ResultResponse<?> findChildID(String code);
    ResultResponse<?> findChildPassword(FindChildPasswordRequestDto request);
}
