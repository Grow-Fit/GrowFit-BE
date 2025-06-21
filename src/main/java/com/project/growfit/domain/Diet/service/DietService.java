package com.project.growfit.domain.Diet.service;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateFoodListRequestDto;
import com.project.growfit.domain.Diet.dto.response.*;
import com.project.growfit.domain.Diet.entity.DietState;
import com.project.growfit.domain.Diet.entity.Sticker;
import com.project.growfit.global.response.ResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DietService {

    List<FoodResponseDto> searchFoods(String keyword, int page, int size);

    FoodResponseDto getFoodDetail(Long foodId);

    DietBasicDto addDiet(AddDietRequestDto dto);
    DietBasicDto updateDiet(Long dietId, UpdateDietRequestDto dto);
    DietBasicDto deleteDiet(Long dietId);
    DietResponseDto getDietDetail(Long dietId);
    DietBasicDto uploadPhoto(Long dietId, MultipartFile image);
    DietBasicDto deletePhoto(Long dietId);
    DietBasicDto updateDietState(Long dietId, DietState dietState);
    DietBasicDto overrideDietNutrition(Long dietId, UpdateFoodListRequestDto dto);
    MonthlyStickerResponseDto getMonthlyStickersByParent(String month);
    DailyDietResponseDto getDailyDietByDate(String date);
    DailyDietResponseDto markSticker(Long dailyDietId, Sticker sticker);
    DailyDietResponseDto updateSticker(Long dailyDietId, Sticker sticker);
    DailyDietResponseDto deleteSticker(Long dailyDietId);
    ResultResponse<?> getDailyDietById(Long dailyId);
}
