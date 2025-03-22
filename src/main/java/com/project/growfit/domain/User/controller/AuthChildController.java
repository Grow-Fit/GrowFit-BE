package com.project.growfit.domain.User.controller;

import com.project.growfit.domain.User.dto.request.ChildCredentialsRequest;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.domain.User.service.AuthChildService;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/child")
@RequiredArgsConstructor
public class AuthChildController {

    private final AuthChildService authChildService;

    @GetMapping("/register/code")
    public ResponseEntity<?> registerChildByCode(@RequestParam String code) {
        ResultResponse<?> resultResponse = authChildService.findByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @PostMapping("/register/{child_id}/credentials")
    public ResponseEntity<?> registerChildCredentials(@PathVariable Long child_id,
                                                      @RequestBody ChildCredentialsRequest request) {
        ResultResponse<?> resultResponse = authChildService.registerChildCredentials(child_id, request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @PutMapping("/{child_id}/nickname")
    public ResponseEntity<?> registerChildPreferences(@PathVariable Long child_id,
                                                      @RequestBody UpdateNicknameRequestDto request) {
        ResultResponse<?> resultResponse = authChildService.updateNickname(child_id, request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginChild(@RequestBody ChildCredentialsRequest request, HttpServletResponse response) {
        ResultResponse<?> resultResponse = authChildService.login(request, response);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);

    }

    @GetMapping("/find/id")
    public ResponseEntity<?> findChildIdByCode(@RequestParam String code) {
        ResultResponse<?> resultResponse = authChildService.findChildID(code);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);

    }

    @PostMapping("/find/password")
    public ResponseEntity<?> resetChildPassword(@RequestBody FindChildPasswordRequestDto request) {
        ResultResponse<?> resultResponse = authChildService.findChildPassword(request);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    /*
    @PutMapping("/{childId}/notification")
    public ResultResponse<?> updateChildNotification(@PathVariable Long childId,
                                                     @RequestBody UpdateChildNotificationRequestDto request) {

        return childService.updateChildNotification(childId, request.notificationEnabled());
    }*/
}
