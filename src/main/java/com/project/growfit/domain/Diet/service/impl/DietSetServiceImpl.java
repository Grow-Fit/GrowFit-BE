package com.project.growfit.domain.Diet.service.impl;

import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
import com.project.growfit.domain.Diet.dto.response.DietSetBasicDto;
import com.project.growfit.domain.Diet.dto.response.DietSetDetailResponseDto;
import com.project.growfit.domain.Diet.dto.response.DietSetResponseDto;
import com.project.growfit.domain.Diet.dto.response.FoodResponseDto;
import com.project.growfit.domain.Diet.entity.DietSet;
import com.project.growfit.domain.Diet.entity.DietSetFood;
import com.project.growfit.domain.Diet.repository.CustomFoodRepository;
import com.project.growfit.domain.Diet.repository.DietSetRepository;
import com.project.growfit.domain.Diet.service.DietSetService;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietSetServiceImpl implements DietSetService {

    private final DietSetRepository dietSetRepository;
    private final CustomFoodRepository customFoodRepository;
    private final FoodApiRepository foodApiRepository;
    private final AuthenticatedUserProvider authenticatedProvider;

    @Override
    @Transactional
    public DietSetBasicDto saveDietSet(SaveDietSetRequestDto dto) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();

        List<DietSetFood> foods = dto.foodList().stream()
                .map(foodItem -> DietSetFood.create(foodItem, foodApiRepository, customFoodRepository, parent))
                .collect(Collectors.toList());

        DietSet dietSet = DietSet.create(dto, parent, foods);
        dietSetRepository.save(dietSet);

        return DietSetBasicDto.toDto(dietSet);
    }

    @Override
    @Transactional
    public DietSetBasicDto updateDietSet(Long dietSetId, SaveDietSetRequestDto dto) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();
        DietSet dietSet = getDietSetOrThrow(dietSetId);

        dietSet.clearFoods();

        List<DietSetFood> updatedFoods = dto.foodList().stream()
                .map(foodItem -> DietSetFood.create(foodItem, foodApiRepository, customFoodRepository, parent))
                .toList();

        dietSet.update(dto.setName(), updatedFoods);

        return DietSetBasicDto.toDto(dietSet);
    }

    @Override
    @Transactional
    public DietSetBasicDto deleteDietSet(Long dietSetId) {
        DietSet dietSet = getDietSetOrThrow(dietSetId);
        dietSetRepository.delete(dietSet);
        return DietSetBasicDto.toDto(dietSet);
    }

    private DietSet getDietSetOrThrow(Long dietSetId) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();
        DietSet dietSet = dietSetRepository.findById(dietSetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_SET_NOT_FOUND));
        if (!dietSet.getParent().equals(parent))
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        return dietSet;
    }
    @Override
    @Transactional(readOnly = true)
    public Page<DietSetResponseDto> getAllDietSets(int page, int size) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DietSet> sets = dietSetRepository.findByParent(parent, pageable);

        return sets.map(set -> new DietSetResponseDto(
                set.getId(),
                set.getSetName(),
                set.getTotalCalorie(),
                set.getFoods().stream().map(DietSetFood::getName).toList()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public DietSetDetailResponseDto getDietSetDetail(Long dietSetId) {
        authenticatedProvider.getAuthenticatedParent();

        DietSet set = getDietSetOrThrow(dietSetId);

        List<FoodResponseDto> foodDetails = set.getFoods().stream().map(f ->
                new FoodResponseDto(
                        f.getName(),
                        f.getCalorie(),
                        f.getCarbohydrate(),
                        f.getFat(),
                        f.getProtein()
                )
        ).toList();

        return new DietSetDetailResponseDto(set.getSetName(), foodDetails);
    }
}
