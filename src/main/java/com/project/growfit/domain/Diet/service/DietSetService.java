package com.project.growfit.domain.Diet.service;

import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
import com.project.growfit.global.response.ResultResponse;

public interface DietSetService {
    ResultResponse<?> saveDietSet(SaveDietSetRequestDto dto);
    ResultResponse<?> getAllDietSets(int page, int size);
    ResultResponse<?> getDietSetDetail(Long dietSetId);

    ResultResponse<?> updateDietSet(Long dietSetId, SaveDietSetRequestDto dto);

    ResultResponse<?> deleteDietSet(Long dietSetId);
}
