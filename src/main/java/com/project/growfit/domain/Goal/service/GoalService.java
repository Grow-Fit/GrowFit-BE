package com.project.growfit.domain.Goal.service;

import com.project.growfit.domain.Goal.dto.request.CreateWeeklyGoalRequestDto;
import com.project.growfit.domain.Goal.dto.request.GoalCertificationRequestDto;
import com.project.growfit.domain.Goal.dto.response.CertificationResponseDto;
import com.project.growfit.domain.Goal.dto.response.GoalResponseDto;
import com.project.growfit.domain.Goal.dto.response.WeeklyGoalHistoryResponseDto;
import com.project.growfit.domain.Goal.dto.response.WeeklyGoalResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface GoalService {
    WeeklyGoalResponseDto createWeeklyGoal(CreateWeeklyGoalRequestDto request);
    WeeklyGoalResponseDto getWeeklyGoal(LocalDate date);

    GoalResponseDto certifyDailyGoal(Long goalId, MultipartFile image);
    List<CertificationResponseDto> getCertificationsByGoalId(Long goalId);

    GoalResponseDto updateGoalTitle(Long goalId, String title);
}
