package com.project.growfit.domain.Goal.service.impl;

import com.project.growfit.domain.Goal.dto.request.CreateWeeklyGoalRequestDto;
import com.project.growfit.domain.Goal.dto.response.CertificationResponseDto;
import com.project.growfit.domain.Goal.dto.response.GoalResponseDto;
import com.project.growfit.domain.Goal.dto.response.WeeklyGoalResponseDto;
import com.project.growfit.domain.Goal.entity.Certification;
import com.project.growfit.domain.Goal.entity.Goal;
import com.project.growfit.domain.Goal.entity.GoalStatus;
import com.project.growfit.domain.Goal.entity.WeeklyGoal;
import com.project.growfit.domain.Goal.repository.CertificationRepository;
import com.project.growfit.domain.Goal.repository.GoalRepository;
import com.project.growfit.domain.Goal.repository.WeeklyGoalRepository;
import com.project.growfit.domain.Goal.service.GoalService;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.s3.service.S3UploadService;
import org.springframework.transaction.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final WeeklyGoalRepository weeklyGoalRepository;
    private final GoalRepository goalRepository;
    private final CertificationRepository certificationRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final S3UploadService s3UploadService;
    private String imageUploadPath = "goal/";

    @Override
    public WeeklyGoalResponseDto createWeeklyGoal(CreateWeeklyGoalRequestDto request) {
        Parent parent = authenticatedUserProvider.getAuthenticatedParent();

        validateDuplicateWeeklyGoal(request, parent);
        WeeklyGoal weeklyGoal = WeeklyGoal.create(
                request.startDate(),
                request.endDate(),
                request.certificationCount(),
                parent,
                request.goals());
        weeklyGoalRepository.save(weeklyGoal);

        return WeeklyGoalResponseDto.toDto(weeklyGoal);
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyGoalResponseDto getWeeklyGoal(LocalDate date) {
        Parent parent = authenticatedUserProvider.getAuthenticatedParent();
        WeeklyGoal weeklyGoal = findWeeklyGoalByDate(date, parent);

        return WeeklyGoalResponseDto.toDto(weeklyGoal);
    }

    @Override
    @Transactional
    public GoalResponseDto certifyDailyGoal(Long goalId, MultipartFile image) {
        Child child = authenticatedUserProvider.getAuthenticatedChild();
        Goal goal = findGoalOrThrow(goalId);
        WeeklyGoal weeklyGoal = goal.getWeeklyGoal();
        LocalDate today = LocalDate.now();

        if (!weeklyGoal.getParent().getChildren().contains(child)) throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        if (image == null || image.isEmpty()) throw new BusinessException(ErrorCode.EMPTY_IMAGE_FILE);
        if (today.isBefore(weeklyGoal.getStartDate()) || today.isAfter(weeklyGoal.getEndDate())) throw new BusinessException(ErrorCode.CERTIFICATION_NOT_ALLOWED_DATE);

        String imageUrl = s3UploadService.saveFile(image, imageUploadPath);
        Certification certification = Certification.create(imageUrl, goal);

        goal.addCertification(certification);
        certificationRepository.save(certification);

        return GoalResponseDto.toDto(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDto> getCertificationsByGoalId(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOAL));

        return goal.getCertificationList().stream()
                .map(CertificationResponseDto::toDto)
                .toList();
    }

    @Override
    @Transactional
    public GoalResponseDto updateGoalTitle(Long goalId, String title) {
        Goal goal = findGoalOrThrow(goalId);
        if (goal.getStatus() != GoalStatus.PENDING) throw new BusinessException(ErrorCode.CANNOT_UPDATE_CERTIFIED_GOAL);
        goal.updateTitle(title);

        return GoalResponseDto.toDto(goal);
    }

    private Goal findGoalOrThrow (Long goalId) {
        return  goalRepository.findById(goalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOAL));
    }

    private WeeklyGoal findWeeklyGoalByDate(LocalDate date, Parent parent) {
        return weeklyGoalRepository.findByParentAndStartDateLessThanEqualAndEndDateGreaterThanEqual(parent, date, date)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_WEEKLY_GOAL));
    }

    private void validateDuplicateWeeklyGoal(CreateWeeklyGoalRequestDto request, Parent parent) {
        boolean exists = weeklyGoalRepository.existsByParentAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                parent, request.endDate(), request.startDate());
        if (exists) throw new BusinessException(ErrorCode.DUPLICATE_WEEKLY_GOAL);
    }
}
