package com.project.growfit.domain.Goal.service.impl;

import com.project.growfit.domain.Goal.dto.request.LetterRequestDto;
import com.project.growfit.domain.Goal.dto.response.LetterBasicResponseDto;
import com.project.growfit.domain.Goal.dto.response.LetterResponseDto;
import com.project.growfit.domain.Goal.entity.GoalStatus;
import com.project.growfit.domain.Goal.entity.Letter;
import com.project.growfit.domain.Goal.entity.WeeklyGoal;
import com.project.growfit.domain.Goal.repository.LetterRepository;
import com.project.growfit.domain.Goal.repository.WeeklyGoalRepository;
import com.project.growfit.domain.Goal.service.LetterService;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LetterServiceImpl implements LetterService {

    private final LetterRepository letterRepository;
    private final WeeklyGoalRepository weeklyGoalRepository;
    private final AuthenticatedUserProvider auth;

    @Override
    @Transactional
    public LetterBasicResponseDto createLetter(Long weeklyGoalId, LetterRequestDto request) {
        Parent parent = auth.getAuthenticatedParent();
        WeeklyGoal weeklyGoal = getWeeklyGoalOrThrow(weeklyGoalId);

        validateParentAccess(weeklyGoal, parent);
        validateAllGoalsComplete(weeklyGoal);
        validateLetterNotSent(weeklyGoal);

        Letter letter = Letter.create(request.content(), weeklyGoal);
        weeklyGoal.markLetterSent();
        letterRepository.save(letter);
        return LetterBasicResponseDto.toDto(letter);
    }

    @Override
    public LetterBasicResponseDto getLetterByWeeklyGoalId(Long weeklyGoalId) {
        WeeklyGoal weeklyGoal = getWeeklyGoalOrThrow(weeklyGoalId);

        Letter letter = letterRepository.findByWeeklyGoal(weeklyGoal)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_LETTER));

        return LetterBasicResponseDto.toDto(letter);
    }

    @Override
    public Page<LetterResponseDto> getAllLetters(Pageable pageable) {
        Long parentId = auth.getAuthenticatedParent().getId();
        return letterRepository.findAllByWeeklyGoal_Parent_Id(parentId, pageable)
                .map(LetterResponseDto::toDto);
    }

    private WeeklyGoal getWeeklyGoalOrThrow(Long weeklyGoalId) {
        return weeklyGoalRepository.findById(weeklyGoalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WEEKLY_GOAL));
    }

    private static void validateParentAccess(WeeklyGoal weeklyGoal, Parent parent) {
        if (!weeklyGoal.getParent().equals(parent)) throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
    }

    private static void validateAllGoalsComplete(WeeklyGoal weeklyGoal) {
        boolean allComplete = weeklyGoal.getGoalList().stream()
                .allMatch(goal -> goal.getStatus() == GoalStatus.COMPLETE);
        if (!allComplete) throw new BusinessException(ErrorCode.NOT_COMPLETED_ALL_GOALS);
    }

    private static void validateLetterNotSent(WeeklyGoal weeklyGoal) {
        if (weeklyGoal.isLetterSent()) throw new BusinessException(ErrorCode.LETTER_ALREADY_SENT);
    }
}