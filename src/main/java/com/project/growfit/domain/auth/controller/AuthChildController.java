package com.project.growfit.domain.auth.controller;

import com.project.growfit.domain.auth.dto.request.AuthChildRequestDto;
import com.project.growfit.domain.auth.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.auth.dto.response.ChildResponseDto;
import com.project.growfit.domain.auth.service.AuthChildService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/child")
@RequiredArgsConstructor
@Tag(name = "아이 로그인 및 회원가입 API", description = "아이 회원가입/로그인 관련 API입니다.")
public class AuthChildController {

    private final AuthChildService authChildService;

    @Operation(summary = "아이 회원가입 시 인증 코드로 정보 조회")
    @GetMapping("/register/code")
    public ResultResponse<ChildResponseDto> registerChildByCode(@NotBlank @RequestParam String code) {
        ChildResponseDto dto = authChildService.findByCode(code);

        return ResultResponse.of(ResultCode.INFO_SUCCESS, dto);
    }

    @Operation(summary = "아이 회원가입 시 아이디 & 비밀번호 & 닉네임 등록")
    @PostMapping("/register/{child_id}/credentials")
    public ResultResponse<ChildInfoResponseDto> registerChildCredentials(@PathVariable Long child_id,
                                                                         @Valid @RequestBody AuthChildRequestDto request) {
        ChildInfoResponseDto dto = authChildService.registerChildCredentials(child_id, request);

        return ResultResponse.of(ResultCode.INFO_REGISTRATION_SUCCESS, dto);
    }

    @Operation(summary = "아이 로그인")
    @PostMapping("/login")
    public ResultResponse<ChildResponseDto> loginChild(@Valid @RequestBody AuthChildRequestDto request, HttpServletResponse response) {
        ChildResponseDto dto = authChildService.login(request, response);

        return ResultResponse.of(ResultCode.LOGIN_SUCCESS, dto);
    }

    @Operation(summary = "아이 로그아웃")
    @PostMapping("/logout")
    public ResultResponse<ChildResponseDto> logoutChild(HttpServletResponse response) {
        ChildResponseDto dto = authChildService.logout(response);

        return ResultResponse.of(ResultCode.LOGOUT_SUCCESS, dto);
    }

    @Operation(summary = "아이 인증코드로 ID 찾기")
    @GetMapping("/find/id")
    public ResultResponse<ChildResponseDto> findChildIdByCode(
            @RequestParam @NotBlank(message = "인증 코드는 필수입니다.") String code){
        ChildResponseDto dto = authChildService.findChildID(code);

        return ResultResponse.of(ResultCode.INFO_SUCCESS, dto);
    }

    @Operation(summary = "아이 비밀번호 재설정")
    @PostMapping("/find/password")
    public ResultResponse<ChildInfoResponseDto> resetChildPassword(@Valid @RequestBody FindChildPasswordRequestDto request) {
        ChildInfoResponseDto dto = authChildService.findChildPassword(request);

        return ResultResponse.of(ResultCode.INFO_REGISTRATION_SUCCESS, dto);
    }
}
