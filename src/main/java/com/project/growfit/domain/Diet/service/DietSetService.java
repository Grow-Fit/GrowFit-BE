package com.project.growfit.domain.Diet.service;

import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
import com.project.growfit.domain.Diet.dto.response.DietSetBasicDto;
import com.project.growfit.domain.Diet.dto.response.DietSetDetailResponseDto;
import com.project.growfit.domain.Diet.dto.response.DietSetResponseDto;
import com.project.growfit.global.response.ResultResponse;
import org.springframework.data.domain.Page;

public interface DietSetService {
    DietSetBasicDto saveDietSet(SaveDietSetRequestDto dto);
    DietSetBasicDto updateDietSet(Long dietSetId, SaveDietSetRequestDto dto);
    DietSetBasicDto deleteDietSet(Long dietSetId);
    Page<DietSetResponseDto> getAllDietSets(int page, int size);
    DietSetDetailResponseDto getDietSetDetail(Long dietSetId);
}
