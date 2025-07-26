package com.project.growfit.domain.User.controller;

import com.project.growfit.domain.User.dto.request.ChildInfoRequestDto;
import com.project.growfit.domain.User.dto.request.ParentInfoRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ParentInfoResponseDto;
import com.project.growfit.domain.User.service.UserService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "유저 정보 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "부모 정보 조회")
    @GetMapping("/parent")
    public ResultResponse<ParentInfoResponseDto> getParentInfo() {
        ParentInfoResponseDto dto = userService.getParentInfo();
        return ResultResponse.of(ResultCode.INFO_SUCCESS, dto);
    }

    @Operation(summary = "아이 정보 조회")
    @GetMapping("/child")
    public ResultResponse<ChildInfoResponseDto> getChildInfo() {
        ChildInfoResponseDto dto = userService.getChildInfo();
        return ResultResponse.of(ResultCode.INFO_SUCCESS, dto);
    }

    @Operation(summary = "부모 정보 수정 (이메일 수정 시 JWT 재발급)")
    @PutMapping(value = "/parent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<String> updateParentInfo(@Valid @RequestPart("info") ParentInfoRequestDto request,
                                                   @RequestPart(value = "image", required = false) MultipartFile image,
                                                   HttpServletResponse response
    ) throws IOException {
        userService.updateParentInfo(request, image, response);
        return ResultResponse.of(ResultCode.UPDATE_SUCCESS, "부모 정보가 수정되었습니다.");
    }

    @Operation(summary = "아이 정보 수정")
    @PutMapping("/child")
    public ResultResponse<String> updateChildInfo(@Valid @RequestBody ChildInfoRequestDto request) {
        userService.updateChildInfo(request);
        return ResultResponse.of(ResultCode.UPDATE_SUCCESS, "아이 정보가 수정되었습니다.");
    }
}
