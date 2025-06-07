package com.project.growfit.domain.User.controller;

import com.google.zxing.WriterException;
import com.project.growfit.domain.User.dto.request.RegisterChildRequest;
import com.project.growfit.domain.User.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.domain.User.service.AuthParentService;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@Tag(name = "Parent Auth API", description = "부모 회원 관련 API")
public class AuthParentController {

    private final AuthParentService parentService;

    @Operation(summary = "부모 닉네임 설정")
    @PostMapping("/nickname")
    public ResponseEntity<?> setParentNickname(@AuthenticationPrincipal CustomUserDetails user,
                                               @RequestBody UpdateNicknameRequestDto request) {
        ResultResponse<?> resultResponse = parentService.updateParentNickname(user, request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "부모 회원가입 시 아이 등록")
    @PostMapping("/child")
    public ResponseEntity<?> registerChild(@AuthenticationPrincipal CustomUserDetails user,
                                           @RequestBody RegisterChildRequest request){
        ResultResponse<?> resultResponse = parentService.registerChild(user, request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "아이 QR 코드 생성")
    @GetMapping("/child/qr")
    public ResponseEntity<?> createQrCode(@AuthenticationPrincipal CustomUserDetails user) throws WriterException {
        ResultResponse<?> resultResponse = parentService.createQR(user);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);

    }
}
