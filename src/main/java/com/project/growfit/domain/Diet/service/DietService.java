package com.project.growfit.domain.Diet.service;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateNutritionRequestDto;
import com.project.growfit.domain.Diet.entity.Sticker;
import com.project.growfit.global.response.ResultResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DietService {

    ResultResponse<?> searchFoods(String keyword, int page, int size);

    ResultResponse<?> getFoodDetail(Long foodId);

    ResultResponse<?> addDiet(AddDietRequestDto dto);

    ResultResponse<?> getDailyDietById(Long dailyId);

    ResultResponse<?> getDailyDietByDate(String date);

    ResultResponse<?> deleteDiet(Long dietId);

    ResultResponse<?> updateDiet(Long dietId, UpdateDietRequestDto dto);

    ResultResponse<?> markSticker(Long dailyDietId, Sticker sticker);

    ResultResponse<?> getMonthlyStickersByParent(String month);

    ResultResponse<?> uploadPhoto(Long dietId, MultipartFile image);
    ResultResponse<?> updateDietFood(Long dietId, String log);

    ResultResponse<?> updateDietTime(Long dietId, String newTime);

    ResultResponse<?> overrideDietNutrition(Long dietId, UpdateNutritionRequestDto dto);

    ResultResponse<?> getDietDetail(Long dietId);

    ResultResponse<?> deletePhoto(Long dietId);

    ResultResponse<?> deleteSticker(Long dailyDietId);

    ResultResponse<?> updateSticker(Long dailyDietId, Sticker sticker);
}
