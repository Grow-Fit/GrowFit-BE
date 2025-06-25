package com.project.growfit.domain.Goal.controller;

import com.project.growfit.domain.Goal.dto.request.CreateWeeklyGoalRequestDto;
import com.project.growfit.domain.Goal.dto.request.GoalCertificationRequestDto;
import com.project.growfit.domain.Goal.dto.response.CertificationResponseDto;
import com.project.growfit.domain.Goal.dto.response.GoalResponseDto;
import com.project.growfit.domain.Goal.dto.response.WeeklyGoalResponseDto;
import com.project.growfit.domain.Goal.service.GoalService;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
@Tag(name = "주간 목표 API", description = "주간 목표 및 일일 인증 관련 API입니다.")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "주간 목표 생성", description = "부모가 주간 목표(WeeklyGoal)를 생성합니다. 시작일, 종료일, 인증 횟수, 목표 리스트를 포함해야 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주간 목표 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류 또는 중복된 주간 목표 존재"),
            @ApiResponse(responseCode = "403", description = "권한 없음")})
    public ResultResponse<WeeklyGoalResponseDto> createWeeklyGoal(@Valid @RequestBody CreateWeeklyGoalRequestDto request) {
        WeeklyGoalResponseDto dto = goalService.createWeeklyGoal(request);

        return ResultResponse.of(ResultCode.WEEKLY_GOAL_CREATE_SUCCESS, dto);
    }

    /* 주간 목표 단일 조회 API (날짜 기준) */
    @GetMapping
    @Operation(summary = "주간 목표 단일 조회", description = "특정 날짜가 속한 주간 목표를 조회합니다. " + "인증된 부모의 목표만 조회 가능합니다.")
    @Parameters({@Parameter(name = "date", description = "조회할 날짜 (yyyy-MM-dd)", required = true, example = "2025-06-24")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주간 목표 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 날짜에 해당하는 주간 목표가 존재하지 않음")})
    public ResultResponse<WeeklyGoalResponseDto> getWeeklyGoal(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        WeeklyGoalResponseDto dto = goalService.getWeeklyGoal(date);

        return ResultResponse.of(ResultCode.WEEKLY_GOAL_FETCH_SUCCESS, dto);
    }

    @PostMapping(value = "/{goalId}/certify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "일일 목표 인증", description = "아이 계정으로 특정 목표에 대한 인증 이미지를 업로드합니다. " + "하루에 한 번만 인증 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목표 인증 성공"),
            @ApiResponse(responseCode = "400", description = "이미 오늘 인증한 경우"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 목표 ID가 존재하지 않음")})
    public ResultResponse<GoalResponseDto> certifyDailyGoal(@PathVariable Long goalId,
                                                            @RequestPart(name = "image", required = false) MultipartFile image) {
        GoalResponseDto dto = goalService.certifyDailyGoal(goalId, image);

        return ResultResponse.of(ResultCode.GOAL_CERTIFICATION_SUCCESS, dto);
    }

    @GetMapping("/{goalId}/certifications")
    @Operation(summary = "목표 인증 내역 조회", description = "특정 목표(goalId)에 등록된 인증 내역 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 목표가 존재하지 않음")})
    public ResultResponse<List<CertificationResponseDto>> getCertifications(@PathVariable Long goalId) {
        List<CertificationResponseDto> dto = goalService.getCertificationsByGoalId(goalId);

        return ResultResponse.of(ResultCode.CERTIFICATION_LIST_FETCH_SUCCESS, dto);
    }

    @PutMapping("/{goalId}")
    @Operation(summary = "목표 수정", description = "특정 목표(goalId) 타이틀(제목)을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "이미 인증을 진행중인 목표는 수정 불가"),
            @ApiResponse(responseCode = "404", description = "해당 목표가 존재하지 않음")})
    public ResultResponse<GoalResponseDto> getCertifications(@PathVariable Long goalId,
                                                                            @Valid @NotBlank @RequestParam String title) {
        GoalResponseDto dto = goalService.updateGoalTitle(goalId, title);

        return ResultResponse.of(ResultCode.GOAL_UPDATE_SUCCESS, dto);
    }
}