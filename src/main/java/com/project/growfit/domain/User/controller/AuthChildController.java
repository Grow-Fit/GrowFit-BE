package com.project.growfit.domain.User.controller;

import com.project.growfit.domain.User.dto.request.ChildCredentialsRequestDto;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.service.AuthChildService;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/child")
@RequiredArgsConstructor
@Tag(name = "Child Auth API", description = "아이 회원가입/로그인 관련 API")
public class AuthChildController {

    private final AuthChildService authChildService;

    @Operation(summary = "아이 회원가입 시 인증 코드로 정보 조회")
    @GetMapping("/register/code")
    public ResponseEntity<?> registerChildByCode(@RequestParam String code) {
        ResultResponse<?> resultResponse = authChildService.findByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "아이 회원가입 시 아이디 & 비밀번호 & 닉네임 등록")
    @PostMapping("/register/{child_id}/credentials")
    public ResponseEntity<?> registerChildCredentials(@PathVariable Long child_id,
                                                      @RequestBody ChildCredentialsRequestDto request) {
        ResultResponse<?> resultResponse = authChildService.registerChildCredentials(child_id, request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "아이 로그인")
    @PostMapping("/login")
    public ResponseEntity<?> loginChild(@RequestBody ChildCredentialsRequestDto request, HttpServletResponse response) {
        ResultResponse<?> resultResponse = authChildService.login(request, response);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);

    }

    @Operation(summary = "아이 인증코드로 ID 찾기")
    @GetMapping("/find/id")
    public ResponseEntity<?> findChildIdByCode(@RequestParam String code) {
        ResultResponse<?> resultResponse = authChildService.findChildID(code);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);

    }

    @Operation(summary = "아이 비밀번호 재설정")
    @PostMapping("/find/password")
    public ResponseEntity<?> resetChildPassword(@RequestBody FindChildPasswordRequestDto request) {
        ResultResponse<?> resultResponse = authChildService.findChildPassword(request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }
}
