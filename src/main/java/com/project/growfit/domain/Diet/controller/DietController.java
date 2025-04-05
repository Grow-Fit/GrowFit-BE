package com.project.growfit.domain.Diet.controller;

import com.project.growfit.domain.Diet.service.DietService;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet")
@Tag(name = "Diet API", description = "식단일지 관련 API")
public class DietController {

    private final DietService dietService;

    @Operation(summary = "음식 검색", description = "키워드로 음식을 검색합니다. 페이징 지원")
    @GetMapping("/search")
    public ResponseEntity<?> searchFoods(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "검색 키워드", example = "닭") @RequestParam String keyword,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        ResultResponse<?> resultResponse = dietService.searchFoods(user, keyword, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }


    @Operation(summary = "음식 상세 조회", description = "특정 음식의 상세 정보를 조회합니다.")
    @GetMapping("/food/{foodId}")
    public ResponseEntity<?> getFoodDetail(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "음식 ID", example = "1") @PathVariable Long foodId
    ) {
        ResultResponse<?> resultResponse = dietService.getFoodDetail(user, foodId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }


}
