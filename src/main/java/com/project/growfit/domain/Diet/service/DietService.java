package com.project.growfit.domain.Diet.service;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.entity.Sticker;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.response.ResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DietService {

    // 1. 음식 검색어로 조회
    ResultResponse<?> searchFoods(CustomUserDetails user, String keyword, int page, int size);

    // 2.  음식 데이터 상세 조회
    ResultResponse<?> getFoodDetail(CustomUserDetails user, Long foodId);

    // 3. 식단 추가
    ResultResponse<?> addDiet(CustomUserDetails user, AddDietRequestDto dto);

    ResultResponse<?> getDailyDietById(Long dailyId);

    ResultResponse<?> getDailyDietByDate(CustomUserDetails user, String date);

    ResultResponse<?> deleteDiet(Long dietId);

    ResultResponse<?> updateDiet(Long dietId, UpdateDietRequestDto dto);

    ResultResponse<?> markSticker(Long dailyDietId, Sticker sticker);

    ResultResponse<?> getMonthlyStickersByParent(CustomUserDetails user, String month);
}
