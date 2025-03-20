package com.project.growfit.domain.auth.service;

import com.project.growfit.domain.auth.dto.request.ParentSignUpRequest;
import com.project.growfit.domain.auth.dto.response.ParentResponse;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface OauthService {
    String getKakaoAccessToken(String code);
    HashMap<String, Object> getUserKakaoInfo(String access_token);
    ResultResponse<?> kakaoLogin(String access_token, HttpServletResponse response);
    ParentResponse findByUserKakaoIdentifier(String kakaoIdentifier);
    Long signUp(ParentSignUpRequest requestDto);
}
