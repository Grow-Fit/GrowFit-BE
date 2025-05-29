package com.project.growfit.domain.Diet.service.impl;

import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
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
import com.project.growfit.global.auto.service.AuthenticatedUserProvider;
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
    public ResultResponse<?> saveDietSet(SaveDietSetRequestDto dto) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();

        List<DietSetFood> foods = dto.foodList().stream()
                .map(foodItem -> DietSetFood.create(foodItem, foodApiRepository, customFoodRepository, parent))
                .collect(Collectors.toList());

        DietSet dietSet = DietSet.create(dto, parent, foods);
        dietSetRepository.save(dietSet);

        return ResultResponse.of(ResultCode.DIET_SET_SAVE_SUCCESS, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getAllDietSets(int page, int size) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DietSet> sets = dietSetRepository.findByParent(parent, pageable);

        Page<DietSetResponseDto> response = sets.map(set -> new DietSetResponseDto(
                set.getId(),
                set.getSetName(),
                set.getTotalCalorie(),
                set.getFoods().stream().map(DietSetFood::getName).toList()
        ));

        return ResultResponse.of(ResultCode.DIET_SET_LIST_SUCCESS, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getDietSetDetail(Long dietSetId) {
        authenticatedProvider.getAuthenticatedParent();

        DietSet set = getDietSetOrThrow(dietSetId);

        List<FoodResponseDto> foodDetails = set.getFoods().stream().map(f ->
                new FoodResponseDto(
                        f.getName(),
                        null, null, null,
                        f.getCalorie(),
                        f.getCarbohydrate(),
                        f.getFat(),
                        f.getProtein()
                )
        ).toList();

        DietSetDetailResponseDto response = new DietSetDetailResponseDto(set.getSetName(), foodDetails);
        return ResultResponse.of(ResultCode.DIET_SET_DETAIL_SUCCESS, response);
    }

    @Override
    @Transactional
    public ResultResponse<?> updateDietSet(Long dietSetId, SaveDietSetRequestDto dto) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();
        DietSet set = getDietSetOrThrow(dietSetId);

        set.clearFoods();

        List<DietSetFood> updatedFoods = dto.foodList().stream()
                .map(foodItem -> DietSetFood.create(foodItem, foodApiRepository, customFoodRepository, parent))
                .toList();

        set.update(dto.setName(), updatedFoods);

        return ResultResponse.of(ResultCode.DIET_SET_EDIT_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> deleteDietSet(Long dietSetId) {
        DietSet set = getDietSetOrThrow(dietSetId);
        dietSetRepository.delete(set);
        return ResultResponse.of(ResultCode.DIET_SET_DELETE_SUCCESS, null);
    }

    private DietSet getDietSetOrThrow(Long dietSetId) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();
        DietSet set = dietSetRepository.findById(dietSetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_SET_NOT_FOUND));
        if (!set.getParent().equals(parent))
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);

        return set;
    }
}
