package com.project.growfit.domain.Diet.controller;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateNutritionRequestDto;
import com.project.growfit.domain.Diet.entity.DietState;
import com.project.growfit.domain.Diet.entity.Sticker;
import com.project.growfit.domain.Diet.service.DietService;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet")
@Tag(name = "Diet API", description = "식단일지 관련 API")
public class DietController {

    private final DietService dietService;

    @Operation(summary = "음식 검색", description = "키워드로 음식을 검색합니다.")
    @GetMapping("/food/search")
    public ResponseEntity<?> searchFoods(@Parameter(description = "검색 키워드", example = "닭") @RequestParam String keyword,
                                         @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
                                         @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
        ResultResponse<?> resultResponse = dietService.searchFoods(keyword, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "음식 상세 조회", description = "특정 음식의 상세 정보를 조회합니다.")
    @GetMapping("/food/{foodId}")
    public ResponseEntity<?> getFoodDetail(@Parameter(description = "음식 ID", example = "1")
                                               @PathVariable Long foodId) {
        ResultResponse<?> resultResponse = dietService.getFoodDetail(foodId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "식단 추가", description = "식단을 추가합니다.")
    @PostMapping(value = "/food")
    public ResponseEntity<?> addDiet(@Valid @RequestBody AddDietRequestDto request) {
        ResultResponse<?> resultResponse = dietService.addDiet(request);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "식단 수정", description = "식단을 수정합니다.")
    @PutMapping("/food/{dietId}")
    public ResponseEntity<?> updateDiet(@PathVariable Long dietId,
                                        @Valid @RequestBody UpdateDietRequestDto request){
        ResultResponse<?> resultResponse = dietService.updateDiet(dietId, request);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "식단 시간 수정", description = "식단의 섭취 시간을 수정합니다.")
    @PatchMapping("/food/{dietId}/time")
    public ResponseEntity<?> updateDietTime(@PathVariable Long dietId,
                                            @RequestParam String updateTime) {
        ResultResponse<?> resultResponse = dietService.updateDietTime(dietId, updateTime);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "식단 제거", description = "식단을 제거합니다.")
    @DeleteMapping("/food/{dietId}")
    public ResponseEntity<?> deleteDiet(@PathVariable Long dietId) {
        ResultResponse<?> resultResponse = dietService.deleteDiet(dietId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "식단 상세 조회", description = "식단 상세 정보를 조회합니다.")
    @GetMapping("/food/info/{dietId}")
    public ResponseEntity<?> getDietDetail(@PathVariable Long dietId) {
        ResultResponse<?> resultResponse = dietService.getDietDetail(dietId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "아이 식단 사진 추가", description = "아이가 저장된 식단에 사진을 추가합니다.")
    @PostMapping(value = "/food/{dietId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDietPhoto(@PathVariable Long dietId,
                                             @RequestPart MultipartFile image) {
        ResultResponse<?> resultResponse = dietService.uploadPhoto(dietId, image);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "아이 식단 섭취 여부 등록", description = "식단 섭취 여부(MATCH/UNMATCH)를 등록합니다. 식단을 어길 시 UNMATCH, 식단을 지킬 시 MATCH")
    @PatchMapping("/food/{dietId}/state")
    public ResponseEntity<?> updateDietState(@PathVariable Long dietId,
                                             @RequestParam DietState dietState) {
        ResultResponse<?> result = dietService.updateDietState(dietId, dietState);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "식단 불이행 시 영양 정보 직접 입력", description = "아이 식단을 지키지 못한 경우 수동으로 영양소를 입력합니다.")
    @PutMapping("/food/{dietId}/override")
    public ResponseEntity<?> overrideDietNutrition(@PathVariable Long dietId,
                                                   @Valid @RequestBody UpdateNutritionRequestDto dto) {
        ResultResponse<?> resultResponse = dietService.overrideDietNutrition(dietId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "월별 식단 요약 조회", description = "월별 식단 작성 여부와 스티커 남김 여부를 조회합니다.")
    @GetMapping("/daily/calendar")
    public ResponseEntity<?> getCalendarOverview(@Parameter(description = "조회할 월 (yyyy-MM)", example = "2025-04")
                                                 @RequestParam("month") String month) {
        ResultResponse<?> resultResponse = dietService.getMonthlyStickersByParent(month);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "날짜별 식단 조회", description = "선택한 날짜의 식단을 조회합니다.")
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyDietByDate(@Parameter(description = "조회할 날짜", example = "2025-04-10")
                                                @RequestParam("date") String date) {
        ResultResponse<?> resultResponse = dietService.getDailyDietByDate(date);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "스티커 업데이트", description = "식단에 스티커를 추가합니다.")
    @PostMapping("/daily/{dailyDietId}/sticker")
    public ResponseEntity<?> markSticker(@PathVariable Long dailyDietId,
                                        @RequestParam Sticker sticker) {
        ResultResponse<?> resultResponse = dietService.markSticker(dailyDietId, sticker);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "아이 식단 사진 삭제", description = "아이가 등록한 식단 사진을 삭제합니다.")
    @DeleteMapping("/food/{dietId}/photo")
    public ResponseEntity<?> deleteDietPhoto(@PathVariable Long dietId) {
        ResultResponse<?> resultResponse = dietService.deletePhoto(dietId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "스티커 수정", description = "기존 스티커를 수정합니다.")
    @PatchMapping("/daily/{dailyDietId}/sticker")
    public ResponseEntity<?> updateSticker(@PathVariable Long dailyDietId,
                                           @RequestParam Sticker sticker) {
        ResultResponse<?> result = dietService.updateSticker(dailyDietId, sticker);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "스티커 삭제", description = "등록된 스티커를 삭제합니다.")
    @DeleteMapping("/daily/{dailyDietId}/sticker")
    public ResponseEntity<?> deleteSticker(@PathVariable Long dailyDietId) {
        ResultResponse<?> resultResponse = dietService.deleteSticker(dailyDietId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }


}
