package com.project.growfit.domain.Diet.controller;

import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
import com.project.growfit.domain.Diet.service.DietSetService;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/diet/set")
@Tag(name = "DietSet API", description = "식단 세트 API")
@RequiredArgsConstructor
public class DietSetController {

    private final DietSetService dietSetService;

    @Operation(summary = "식단 세트 저장", description = "사용자가 식단 세트를 저장합니다.")
    @PostMapping
    public ResponseEntity<?> saveDietSet(@Valid @RequestBody SaveDietSetRequestDto dto) {
        ResultResponse<?> resultResponse = dietSetService.saveDietSet(dto);

        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "모든 식단 세트 조회", description = "사용자가 저장한 모든 식단 세트를 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getDietSetList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dietSetService.getAllDietSets(page, size));
    }

    @Operation(summary = "식단 세트 상세 조회", description = "사용자의 식단 세트의 상세 내용을 조회합니다.")
    @GetMapping("{dietSetId}")
    public ResponseEntity<?> getDietSetDetail(@PathVariable Long dietSetId) {
        return ResponseEntity.ok(dietSetService.getDietSetDetail(dietSetId));
    }

    @Operation(summary = "식단 세트 수정", description = "기존 식단 세트를 수정합니다.")
    @PutMapping("/{dietSetId}")
    public ResponseEntity<?> updateDietSet(
            @PathVariable Long dietSetId,
            @Valid @RequestBody SaveDietSetRequestDto dto) {
        ResultResponse<?> resultResponse = dietSetService.updateDietSet(dietSetId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }

    @Operation(summary = "식단 세트 삭제", description = "식단 세트를 삭제합니다.")
    @DeleteMapping("/{dietSetId}")
    public ResponseEntity<?> deleteDietSet(@PathVariable Long dietSetId) {
        ResultResponse<?> resultResponse = dietSetService.deleteDietSet(dietSetId);
        return ResponseEntity.status(HttpStatus.OK).body(resultResponse);
    }
}