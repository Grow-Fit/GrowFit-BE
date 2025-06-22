package com.project.growfit.domain.Diet.controller;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateFoodListRequestDto;
import com.project.growfit.domain.Diet.dto.response.*;
import com.project.growfit.domain.Diet.entity.DietState;
import com.project.growfit.domain.Diet.entity.Sticker;
import com.project.growfit.domain.Diet.service.DietService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diet")
@Tag(name = "식단 관련 API", description = "식단일지 관련 API입니다.")
public class DietController {

    private final DietService dietService;

    @Operation(summary = "음식 검색", description = "키워드로 음식을 검색합니다.")
    @GetMapping("/food/search")
    public ResultResponse<List<FoodResponseDto>> searchFoods(@Parameter(description = "검색 키워드", example = "닭") @RequestParam @NotBlank(message = "검색 키워드를 입력해주세요.") String keyword,
                                                             @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,
                                                             @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") @Min(1) int size) {

        List<FoodResponseDto> dtos = dietService.searchFoods(keyword, page, size);

        return dtos.isEmpty()
                ? ResultResponse.of(ResultCode.DIET_SEARCH_RESULT_EMPTY, dtos)
                : ResultResponse.of(ResultCode.DIET_SEARCH_SUCCESS, dtos);
    }

    @Operation(summary = "음식 상세 조회", description = "특정 음식의 상세 정보를 조회합니다.")
    @GetMapping("/food/{foodId}")
    public ResultResponse<FoodResponseDto> getFoodDetail(@Parameter(description = "음식 ID", example = "1")
                                               @PathVariable Long foodId) {
        FoodResponseDto dto = dietService.getFoodDetail(foodId);
        return ResultResponse.of(ResultCode.DIET_DETAIL_RETRIEVAL_SUCCESS, dto);
    }

    @Operation(summary = "식단 추가", description = "식단을 추가합니다.")
    @PostMapping(value = "/food")
    public ResultResponse<DietBasicDto>addDiet(@Valid @RequestBody AddDietRequestDto request) {
        DietBasicDto dto = dietService.addDiet(request);

        return ResultResponse.of(ResultCode.DIET_ADD_SUCCESS, dto);
    }

    @Operation(summary = "식단 수정", description = "식단을 수정합니다.")
    @PatchMapping("/food/{dietId}")
    public ResultResponse<DietBasicDto> updateDiet(@PathVariable Long dietId,
                                        @Valid @RequestBody UpdateDietRequestDto request){
        DietBasicDto dto = dietService.updateDiet(dietId, request);

        return ResultResponse.of(ResultCode.DIET_EDIT_SUCCESS, dto);
    }

    @Operation(summary = "식단 제거", description = "식단을 제거합니다.")
    @DeleteMapping("/food/{dietId}")
    public ResultResponse<DietBasicDto> deleteDiet(@PathVariable Long dietId) {
        DietBasicDto dto = dietService.deleteDiet(dietId);

        return ResultResponse.of(ResultCode.DIET_DELETE_SUCCESS, dto);
    }

    @Operation(summary = "식단 상세 조회", description = "식단 상세 정보를 조회합니다.")
    @GetMapping("/food/info/{dietId}")
    public ResultResponse<DietResponseDto> getDietDetail(@PathVariable Long dietId) {
        DietResponseDto dto = dietService.getDietDetail(dietId);

        return ResultResponse.of(ResultCode.DIET_DETAIL_RETRIEVAL_SUCCESS, dto);
    }

    @Operation(summary = "식단 완료 제출", description = "식단 사진과 식단 상태(MATCH/UNMATCH)를 함께 제출합니다.")
    @PostMapping(value = "/food/{dietId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<DietBasicDto> submitDiet(
            @PathVariable Long dietId,
            @RequestPart(name = "image", required = false) MultipartFile image,
            @RequestPart(name = "dietState") DietState dietState) {

        DietBasicDto dto = dietService.submitDiet(dietId, image, dietState);
        return ResultResponse.of(ResultCode.DIET_SUBMIT_SUCCESS, dto);
    }

    @Operation(summary = "식단 불이행 시 영양 정보 직접 입력", description = "아이 식단을 지키지 못한 경우 음식 리스트를 수정합니다.")
    @PutMapping("/food/{dietId}/override")
    public ResultResponse<DietBasicDto> overrideDietNutrition(@PathVariable Long dietId,
                                                   @Valid @RequestBody UpdateFoodListRequestDto request) {
        DietBasicDto dto = dietService.overrideDietNutrition(dietId, request);

        return ResultResponse.of(ResultCode.DIET_OVERRIDE_SUCCESS, dto);
    }

    @Operation(summary = "월별 식단 요약 조회", description = "월별 식단 작성 여부와 스티커 남김 여부를 조회합니다.")
    @GetMapping("/daily/calendar")
    public ResultResponse<MonthlyStickerResponseDto> getCalendarOverview(@Parameter(description = "조회할 월 (yyyy-MM)", example = "2025-04")
                                                 @RequestParam("month") String month) {
        MonthlyStickerResponseDto dto = dietService.getMonthlyStickersByParent(month);
        return dto.monthlyStickers().isEmpty()
                ? ResultResponse.of(ResultCode.DIET_DATE_EMPTY, dto)
                : ResultResponse.of(ResultCode.CALENDAR_OVERVIEW_SUCCESS, dto);
    }

    @Operation(summary = "날짜별 식단 조회", description = "선택한 날짜의 식단을 조회합니다.")
    @GetMapping("/daily")
    public ResultResponse<DailyDietResponseDto> getDailyDietByDate(@Parameter(description = "조회할 날짜", example = "2025-04-10")
                                                @RequestParam("date") String date) {
        DailyDietResponseDto dto = dietService.getDailyDietByDate(date);
        return ResultResponse.of(ResultCode.DAILY_DIET_RETRIEVAL_SUCCESS, dto);
    }

    @Operation(summary = "스티커 업데이트", description = "식단에 스티커를 추가합니다.")
    @PostMapping("/daily/{dailyDietId}/sticker")
    public ResultResponse<DailyDietResponseDto> markSticker(@PathVariable Long dailyDietId,
                                                            @RequestParam Sticker sticker) {
        DailyDietResponseDto dto = dietService.markSticker(dailyDietId, sticker);
        return ResultResponse.of(ResultCode.STICKER_MARK_SUCCESS, dto);
    }

    @Operation(summary = "스티커 수정", description = "기존 스티커를 수정합니다.")
    @PatchMapping("/daily/{dailyDietId}/sticker")
    public ResultResponse<DailyDietResponseDto> updateSticker(@PathVariable Long dailyDietId,
                                           @RequestParam Sticker sticker) {
        DailyDietResponseDto dto = dietService.updateSticker(dailyDietId, sticker);
        return ResultResponse.of(ResultCode.STICKER_UPDATE_SUCCESS, dto);
    }

    @Operation(summary = "스티커 삭제", description = "등록된 스티커를 삭제합니다.")
    @DeleteMapping("/daily/{dailyDietId}/sticker")
    public ResultResponse<DailyDietResponseDto> deleteSticker(@PathVariable Long dailyDietId) {
        DailyDietResponseDto dto = dietService.deleteSticker(dailyDietId);
        return ResultResponse.of(ResultCode.STICKER_DELETE_SUCCESS, dto);
    }


}
