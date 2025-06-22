package com.project.growfit.domain.User.service;

import com.project.growfit.domain.User.dto.request.ParentOAuthRequestDto;
import com.project.growfit.domain.User.dto.response.ParentResponseDto;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface OauthService {
    String getKakaoAccessToken(String code);
    HashMap<String, Object> getUserKakaoInfo(String access_token);
    ResultResponse<?> kakaoLogin(String access_token, HttpServletResponse response);
    String kakaoLogout(String access_token, HttpServletResponse response);
    ParentResponseDto findByUserKakaoIdentifier(String kakaoIdentifier);
    Long signUp(ParentOAuthRequestDto requestDto);


}
