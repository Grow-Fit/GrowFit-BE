package com.project.growfit.domain.Diet.controller;

import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
import com.project.growfit.domain.Diet.dto.response.DietSetBasicDto;
import com.project.growfit.domain.Diet.dto.response.DietSetDetailResponseDto;
import com.project.growfit.domain.Diet.dto.response.DietSetResponseDto;
import com.project.growfit.domain.Diet.service.DietSetService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/diet/set")
@Tag(name = "식단세트 관련 API", description = "식단세트 관련 API입니다.")
@RequiredArgsConstructor
public class DietSetController {

    private final DietSetService dietSetService;

    @Operation(summary = "식단 세트 저장", description = "사용자가 식단 세트를 저장합니다.")
    @PostMapping
    public ResultResponse<DietSetBasicDto> saveDietSet(@Valid @RequestBody SaveDietSetRequestDto request) {
        DietSetBasicDto dto = dietSetService.saveDietSet(request);

        return ResultResponse.of(ResultCode.DIET_SET_SAVE_SUCCESS, dto);
    }

    @Operation(summary = "식단 세트 수정", description = "기존 식단 세트를 수정합니다.")
    @PutMapping("/{dietSetId}")
    public ResultResponse<DietSetBasicDto> updateDietSet(
            @PathVariable Long dietSetId,
            @Valid @RequestBody SaveDietSetRequestDto request) {
        DietSetBasicDto dto = dietSetService.updateDietSet(dietSetId, request);
        return ResultResponse.of(ResultCode.DIET_SET_EDIT_SUCCESS, dto);
    }

    @Operation(summary = "식단 세트 삭제", description = "식단 세트를 삭제합니다.")
    @DeleteMapping("/{dietSetId}")
    public ResultResponse<DietSetBasicDto> deleteDietSet(@PathVariable Long dietSetId) {
        DietSetBasicDto dto = dietSetService.deleteDietSet(dietSetId);
        return ResultResponse.of(ResultCode.DIET_SET_DELETE_SUCCESS, dto);
    }

    @Operation(summary = "모든 식단 세트 조회", description = "사용자가 저장한 모든 식단 세트를 페이지 단위로 조회합니다.")
    @GetMapping
    public ResultResponse<Page<DietSetResponseDto>> getDietSetList(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Page<DietSetResponseDto> dto = dietSetService.getAllDietSets(page, size);

        return ResultResponse.of(ResultCode.DIET_SET_LIST_SUCCESS, dto);
    }

    @Operation(summary = "식단 세트 상세 조회", description = "사용자의 식단 세트의 상세 내용을 조회합니다.")
    @GetMapping("{dietSetId}")
    public ResultResponse<DietSetDetailResponseDto> getDietSetDetail(@PathVariable Long dietSetId) {
        DietSetDetailResponseDto dto = dietSetService.getDietSetDetail(dietSetId);

        return ResultResponse.of(ResultCode.DIET_SET_RETRIEVAL_SUCCESS, dto);
    }
}