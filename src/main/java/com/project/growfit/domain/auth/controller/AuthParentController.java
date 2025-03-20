package com.project.growfit.domain.auth.controller;

import com.google.zxing.WriterException;
import com.project.growfit.domain.auth.dto.request.RegisterChildRequest;
import com.project.growfit.domain.auth.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.domain.auth.entity.Parent;
import com.project.growfit.domain.auth.repository.ParentRepository;
import com.project.growfit.domain.auth.service.AuthParentService;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
public class AuthParentController {

    private final AuthParentService parentService;
    private final ParentRepository parentRepository;

    @GetMapping("/test")
    public String test(){
        return "ok";
    }


    @PostMapping("/nickname")
    public ResponseEntity<?> setParentNickname(@AuthenticationPrincipal CustomUserDetails user,
                                               @RequestBody UpdateNicknameRequestDto request) {
        ResultResponse<?> resultResponse = parentService.updateParentNickname(user, request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @PostMapping("/child")
    public ResponseEntity<?> registerChild(@AuthenticationPrincipal CustomUserDetails user,
                                           @RequestBody RegisterChildRequest request){
        ResultResponse<?> resultResponse = parentService.registerChild(user, request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @GetMapping("/child/{child_id}/qr")
    public ResponseEntity<?> createQrCode(@AuthenticationPrincipal CustomUserDetails user,
                                          @PathVariable("child_id") Long  child_id) throws WriterException {
        ResultResponse<?> resultResponse = parentService.createQR(user, child_id);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);

    }

    @GetMapping("/me")
    public ResponseEntity<?> testUserInfo(@AuthenticationPrincipal CustomUserDetails user) {
        String email = user.getUsername();
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.status(HttpStatus.OK).body(parent);
    }

}
