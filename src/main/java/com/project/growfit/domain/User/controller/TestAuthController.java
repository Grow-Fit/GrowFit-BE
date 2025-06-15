package com.project.growfit.domain.User.controller;

import com.project.growfit.domain.User.dto.response.ParentLoginResponseDto;
import com.project.growfit.global.auth.cookie.CookieService;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.redis.entity.TokenRedis;
import com.project.growfit.global.redis.repository.TokenRedisRepository;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestAuthController {

    private final JwtProvider jwtProvider;
    private final TokenRedisRepository tokenRedisRepository;
    private final CookieService cookieService;

    @PostMapping("/generate-token")
    @Operation(summary = "테스트 계정 토큰 변환 api")
    public ResultResponse<?> generateToken(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String email = request.get("email");
        String newAccessToken = jwtProvider.createAccessToken(email, "ROLE_PARENT", "SOCIAL_KAKAO");
        String newRefreshToken = jwtProvider.createRefreshToken(email);
        tokenRedisRepository.save(new TokenRedis(email, newAccessToken, newRefreshToken));
        cookieService.saveAccessTokenToCookie(response, newAccessToken);

        log.info("로그인 성공: email={}, accessToken 저장 완료", email);
        ParentLoginResponseDto dto = new ParentLoginResponseDto(email, true);
        return new ResultResponse<>(ResultCode.LOGIN_SUCCESS, dto);
    }
}
