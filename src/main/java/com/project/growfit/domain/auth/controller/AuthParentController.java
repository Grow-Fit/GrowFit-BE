package com.project.growfit.domain.auth.controller;

import com.google.zxing.WriterException;
import com.project.growfit.domain.auth.dto.request.AuthParentRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.auth.dto.response.ChildQrCodeResponseDto;
import com.project.growfit.domain.auth.service.AuthParentService;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@Tag(name = "부모 로그인 및 회원가입 API", description = "부모 회원가입/로그인 관련 API입니다.")
public class AuthParentController {

    private final AuthParentService parentService;

    @Operation(summary = "부모 회원가입 시 아이 등록")
    @PostMapping("/child")
    public ResultResponse<ChildInfoResponseDto> registerChild(@Valid @RequestBody AuthParentRequestDto request){
        ChildInfoResponseDto dto = parentService.registerChild(request);

        return ResultResponse.of(ResultCode.SIGNUP_SUCCESS, dto);
    }

    @Operation(summary = "아이 QR 코드 생성")
    @GetMapping("/child/qr")
    public ResultResponse<ChildQrCodeResponseDto> createQrCode() throws WriterException {
        ChildQrCodeResponseDto dto = parentService.createQR();

        return ResultResponse.of(ResultCode.QR_GENERATION_SUCCESS, dto);

    }
}
