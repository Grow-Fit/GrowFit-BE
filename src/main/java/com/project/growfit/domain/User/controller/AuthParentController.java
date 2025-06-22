package com.project.growfit.domain.User.controller;

import com.google.zxing.WriterException;
import com.project.growfit.domain.User.dto.request.AuthParentRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ChildQrCodeResponseDto;
import com.project.growfit.domain.User.service.AuthParentService;
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
@Tag(name = "Parent Auth API", description = "부모 회원 관련 API")
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
    public ResultResponse<ChildQrCodeResponseDto> createQrCode(@AuthenticationPrincipal CustomUserDetails user) throws WriterException {
        ChildQrCodeResponseDto dto = parentService.createQR();

        return ResultResponse.of(ResultCode.QR_GENERATION_SUCCESS, dto);

    }
}
