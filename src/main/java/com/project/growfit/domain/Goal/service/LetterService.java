package com.project.growfit.domain.Goal.service;

import com.project.growfit.domain.Goal.dto.request.LetterRequestDto;
import com.project.growfit.domain.Goal.dto.response.LetterBasicResponseDto;
import com.project.growfit.domain.Goal.dto.response.LetterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LetterService {
    LetterBasicResponseDto createLetter(Long weeklyGoalId, LetterRequestDto request);
    LetterBasicResponseDto getLetterByWeeklyGoalId(Long weeklyGoalId);
    Page<LetterResponseDto>  getAllLetters(Pageable pageable);
}