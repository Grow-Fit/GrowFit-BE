
package com.project.growfit.domain.Diet.service.impl;

import com.project.growfit.domain.Diet.dto.response.FoodResponseDto;
import com.project.growfit.domain.Diet.service.DietService;
import com.project.growfit.global.api.entity.FoodApi;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietServiceImpl implements DietService {

    private final FoodApiRepository foodApiRepository;

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> searchFoods(CustomUserDetails user, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<FoodApi> foodPage = foodApiRepository.findByFoodNmContaining(keyword, pageRequest);

        List<FoodResponseDto> list = foodPage.stream()
                .map(FoodResponseDto::toDto)
                .toList();

        if (list.isEmpty())
            return ResultResponse.of(ResultCode.DIET_SEARCH_RESULT_EMPTY, list);
        return ResultResponse.of(ResultCode.DIET_SEARCH_SUCCESS, list);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getFoodDetail(CustomUserDetails user, Long foodId) {
        FoodApi foodApi = findFoodOrThrow(foodId);
        FoodResponseDto dto = FoodResponseDto.toDto(foodApi);

        return ResultResponse.of(ResultCode.DIET_DETAIL_RETRIEVAL_SUCCESS, dto);
    }

    private FoodApi findFoodOrThrow(Long foodId) {
        return foodApiRepository.findById(foodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND));
    }
}

