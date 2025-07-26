package com.project.growfit.domain.auth.controller;

import com.project.growfit.domain.auth.dto.response.ParentLoginResponseDto;
import com.project.growfit.domain.auth.service.OauthService;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Tag(name = "부모 소셜 로그인 API", description = "부모 소셜 로그인(카카오) 관련 API입니다.")
public class OAuthController {

    @Value("${custom.oauth2.kakao.redirect.new-user}")
    private String newUserRedirectUrl;

    @Value("${custom.oauth2.kakao.redirect.existing-user}")
    private String existingUserRedirectUrl;

    private final OauthService oauthService;

    @Operation(summary = "카카오 소셜 로그인 API")
    @GetMapping("/callback/kakao")
    public void kakaoLogin(@RequestParam(value = "code", required = false) String code,
                                                   HttpServletResponse response) {
        if (code == null) {
            try {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "code 파라미터가 없습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            String accessToken = oauthService.getKakaoAccessToken(code);
            ResultResponse<?> resultResponse = oauthService.kakaoLogin(accessToken, response);
            ParentLoginResponseDto dto = (ParentLoginResponseDto) resultResponse.getData();
            String redirectUrl = dto.isNewUser()
                    ? newUserRedirectUrl
                    : existingUserRedirectUrl;
            response.sendRedirect(redirectUrl);
        } catch (BusinessException e) {
            try {
                response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "카카오 로그인 처리 중 오류 발생");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Operation(summary = "카카오 소셜 로그아웃 API")
    @PostMapping("/logout")
    public ResultResponse<String> kakaoLogout(@RequestParam(value = "code", required = false) String code,
                                                        HttpServletResponse response) {
        String message = oauthService.kakaoLogout(code, response);
        return ResultResponse.of(ResultCode.LOGOUT_SUCCESS, message);

    }
}
