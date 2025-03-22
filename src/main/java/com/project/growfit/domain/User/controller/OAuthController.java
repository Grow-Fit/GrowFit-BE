package com.project.growfit.domain.User.controller;

import com.project.growfit.domain.User.service.OauthService;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OauthService oauthService;

    @Operation(summary = "카카오 소셜 로그인 콜백 컨트롤러 입니다.")
    @GetMapping("/callback/kakao")
    public ResponseEntity<?> getKaKaoAuthorizeCode(@RequestParam(value = "code", required = false) String code,
                                                   HttpServletResponse response) {
        if (code == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("code 파라미터가 없습니다.");
        try {
            String accessToken = oauthService.getKakaoAccessToken(code);
            ResultResponse<?> resultResponse = oauthService.kakaoLogin(accessToken, response);
            return ResponseEntity.status(HttpStatus.OK).body(resultResponse);

        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 로그인 처리 중 오류 발생");
        }
    }
}
