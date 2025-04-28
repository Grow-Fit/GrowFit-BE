package com.project.growfit.domain.Diet.controller;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.entity.Sticker;
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

    //키워드로 음식 검색
    @Operation(summary = "음식 검색", description = "키워드로 음식을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<?> searchFoods(@AuthenticationPrincipal CustomUserDetails user,
                                         @Parameter(description = "검색 키워드", example = "닭") @RequestParam String keyword,
                                         @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
                                         @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        ResultResponse<?> resultResponse = dietService.searchFoods(user, keyword, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    //특정 음식 상세 정보 출력
    @Operation(summary = "음식 상세 조회", description = "특정 음식의 상세 정보를 조회합니다.")
    @GetMapping("/food/{foodId}")
    public ResponseEntity<?> getFoodDetail(@AuthenticationPrincipal CustomUserDetails user,
                                           @Parameter(description = "음식 ID", example = "1") @PathVariable Long foodId
    ) {
        ResultResponse<?> resultResponse = dietService.getFoodDetail(user, foodId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    // 부모가 식단을 추가
    @Operation(summary = "식단 추가", description = "식단을 추가합니다.")
    @PostMapping(value = "/food/add")
    public ResponseEntity<?> addDiet(@AuthenticationPrincipal CustomUserDetails user,
                                     @RequestBody AddDietRequestDto request) {
        ResultResponse<?> resultResponse = dietService.addDiet(user, request);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

/*    @Operation(summary = "ID로 하루 식단 조회", description = "dailyId로 하루 식단을 조회합니다.")
    @GetMapping("/daily/{dailyDietId}")
    public ResponseEntity<?> getDailyDietById(@PathVariable Long dailyDietId) {
        ResultResponse<?> resultResponse = dietService.getDailyDietById(dailyDietId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }*/

    //날짜 별 하루 식단 조회
    @Operation(summary = "날짜별 식단 조회", description = "선택한 날짜의 식단을 조회합니다.")
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyDietByDate(@AuthenticationPrincipal CustomUserDetails user,
                                                @Parameter(description = "조회할 날짜", example = "2025-04-10")
                                                @RequestParam("date") String date) {
        ResultResponse<?> resultResponse = dietService.getDailyDietByDate(user, date);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    // 식단 수정

    @Operation(summary = "식단 수정", description = "식단을 수정합니다.")
    @PutMapping("/food/{dietId}")
    public ResponseEntity<?> updateDiet(@PathVariable Long dietId,
                                      @RequestBody UpdateDietRequestDto request){
        ResultResponse<?> resultResponse = dietService.updateDiet(dietId, request);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    //식단 삭제
    @Operation(summary = "식단 제거", description = "식단을 제거합니다.")
    @DeleteMapping("/food/{dietId}")
    public ResponseEntity<?> deleteDiet(@PathVariable Long dietId) {
        ResultResponse<?> resultResponse = dietService.deleteDiet(dietId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    //식단 스티커 생성 & 수정
    @Operation(summary = "스티커 업데이트", description = "식단에 스티커를 추가합니다.")
    @PostMapping("/daily/{dailyDietId}/sticker")
    public ResponseEntity<?> markSticker(@PathVariable Long dailyDietId,
                                        @RequestParam Sticker sticker) {
        ResultResponse<?> resultResponse = dietService.markSticker(dailyDietId, sticker);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "월별 식단 요약 조회", description = "월별 식단 작성 여부와 스티커 남김 여부를 조회합니다.")
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendarOverview(@AuthenticationPrincipal CustomUserDetails user,
                                                 @Parameter(description = "조회할 월 (yyyy-MM)", example = "2025-04")
                                                 @RequestParam("month") String month) {
        ResultResponse<?> resultResponse = dietService.getMonthlyStickersByParent(user, month);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }
}
