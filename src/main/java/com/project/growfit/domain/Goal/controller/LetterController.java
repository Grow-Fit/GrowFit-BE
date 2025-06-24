package com.project.growfit.domain.Goal.controller;

import com.project.growfit.domain.Goal.dto.request.LetterRequestDto;
import com.project.growfit.domain.Goal.dto.response.LetterBasicResponseDto;
import com.project.growfit.domain.Goal.dto.response.LetterResponseDto;
import com.project.growfit.domain.Goal.service.LetterService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/letter")
@RequiredArgsConstructor
@Tag(name = "칭찬 편지 API", description = "주간 목표가 완료되면 부모가 작성하고 아이가 조회할 수 있는 편지 관련 API입니다.")
public class LetterController {

    private final LetterService letterService;

    @PostMapping("/{weeklyGoalId}")
    @Operation(summary = "편지 작성", description = "모든 목표가 COMPLETE 상태일 때 편지를 작성할 수 있습니다.")
    public ResultResponse<LetterBasicResponseDto> createLetter(@PathVariable Long weeklyGoalId,
                                                               @RequestBody @Valid LetterRequestDto request) {
        LetterBasicResponseDto dto = letterService.createLetter(weeklyGoalId, request);
        return ResultResponse.of(ResultCode.LETTER_CREATE_SUCCESS, dto);
    }

    @GetMapping("/{weeklyGoalId}")
    @Operation(summary = "편지 조회", description = "아이 또는 부모가 주간 목표에 작성된 편지를 조회합니다.")
    public ResultResponse<LetterBasicResponseDto> getLetter(@PathVariable Long weeklyGoalId) {
        LetterBasicResponseDto dto = letterService.getLetterByWeeklyGoalId(weeklyGoalId);
        return ResultResponse.of(ResultCode.LETTER_FETCH_SUCCESS, dto);
    }

    @GetMapping()
    @Operation(summary = "모든 편지 페이징 조회", description = "부모가 지금까지 작성한 모든 편지를 페이징으로 조회합니다.")
    public ResultResponse<Page<LetterResponseDto> > getAllLetters(@PageableDefault Pageable pageable) {
        Page<LetterResponseDto>  dto = letterService.getAllLetters(pageable);
        return ResultResponse.of(ResultCode.LETTER_LIST_FETCH_SUCCESS, dto);
    }
}