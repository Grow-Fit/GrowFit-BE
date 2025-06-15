
package com.project.growfit.domain.Diet.service.impl;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.FoodItemDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;

import com.project.growfit.domain.Diet.dto.request.UpdateFoodListRequestDto;
import com.project.growfit.domain.Diet.dto.response.*;
import com.project.growfit.domain.Diet.entity.*;
import com.project.growfit.domain.Diet.repository.CustomFoodRepository;
import com.project.growfit.domain.Diet.repository.DailyDietRepository;
import com.project.growfit.domain.Diet.repository.DietRepository;
import com.project.growfit.domain.Diet.service.DietService;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.api.entity.FoodApi;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import com.project.growfit.global.s3.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietServiceImpl implements DietService {

    private final FoodApiRepository foodApiRepository;
    private final S3UploadService s3UploadService;
    private final DailyDietRepository dailyDietRepository;
    private final CustomFoodRepository customFoodRepository;
    private final DietRepository dietRepository;
    private final AuthenticatedUserProvider authenticatedProvider;

    private String imageUploadPath = "diet/";

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> searchFoods(String keyword, int page, int size) {
        authenticatedProvider.getAuthenticatedParent();
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<FoodApi> foodPage = foodApiRepository.findByFoodNmContaining(keyword, pageRequest);

        List<FoodResponseDto> response = foodPage.stream()
                .map(FoodResponseDto::toDto)
                .toList();

        return response.isEmpty()
                ? ResultResponse.of(ResultCode.DIET_SEARCH_RESULT_EMPTY, response)
                : ResultResponse.of(ResultCode.DIET_SEARCH_SUCCESS, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getFoodDetail(Long foodId) {
        authenticatedProvider.getAuthenticatedParent();
        FoodApi foodApi = findFoodOrThrow(foodId);
        FoodResponseDto response = FoodResponseDto.toDto(foodApi);

        return ResultResponse.of(ResultCode.DIET_DETAIL_RETRIEVAL_SUCCESS, response);
    }


    @Override
    @Transactional
    public ResultResponse<?> addDiet(AddDietRequestDto dto) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();
        Child child = authenticatedProvider.getAuthenticatedChild();
        DailyDiet dailyDiet = getOrCreateDailyDiet(child, dto.date());
        List<Food> foodList = createFoodListFromDto(dto.foodList(), parent);

        Diet diet = Diet.create(dto.eatTime(), dto.mealType(), dailyDiet, child, foodList);
        dailyDiet.addDiet(diet);
        dailyDietRepository.save(dailyDiet);

        return ResultResponse.of(ResultCode.DIET_ADD_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> getDailyDietById(Long dailyId) {
        Child child = authenticatedProvider.getAuthenticatedChild();
        DailyDiet dailyDiet = 	getDailyDietOrThrow(dailyId);

        if (!dailyDiet.getChild().equals(child)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        return ResultResponse.of(
                ResultCode.DIET_RETRIEVAL_SUCCESS,
                new DailyDietResponseDto(
                        dailyDiet.getId(),
                        dailyDiet.getDate().toString(),
                        dailyDiet.getTotalCalorie(),
                        groupDietsByMeal(dailyDiet.getDiets())
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getDailyDietByDate(String date) {
        Child child = authenticatedProvider.getAuthenticatedChild();
        LocalDate parsedDate = LocalDate.parse(date);
        DailyDiet dailyDiet = dailyDietRepository.findByChildAndDate(child, parsedDate)
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_DIET_NOT_FOUND));

        return ResultResponse.of(
                ResultCode.DAILY_DIET_RETRIEVAL_SUCCESS,
                new DailyDietResponseDto(
                        dailyDiet.getId(),
                        dailyDiet.getDate().toString(),
                        dailyDiet.getTotalCalorie(),
                        groupDietsByMeal(dailyDiet.getDiets())
                )
        );
    }

    @Override
    @Transactional
    public ResultResponse<?> deleteDiet(Long dietId) {
        authenticatedProvider.getAuthenticatedParent();
        Diet diet = getDietOrThrow(dietId);
        dietRepository.delete(diet);
        return ResultResponse.of(ResultCode.DIET_DELETE_SUCCESS, null);

    }

    @Override
    @Transactional
    public ResultResponse<?> updateDiet(Long dietId, UpdateDietRequestDto dto) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();
        Diet diet = getDietOrThrow(dietId);
        DailyDiet dailyDiet = diet.getDailyDiet();
        List<Food> foodList = createFoodListFromDto(dto.foodList(), parent);

        applyNewFoodList(diet, dailyDiet, foodList, dto);
        return ResultResponse.of(ResultCode.DIET_EDIT_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> overrideDietNutrition(Long dietId, UpdateFoodListRequestDto dto) {
        Parent parent = authenticatedProvider.getAuthenticatedParent();
        Diet diet = getDietOrThrow(dietId);
        DailyDiet dailyDiet = diet.getDailyDiet();
        List<Food> foodList = createFoodListFromDto(dto.foodList(), parent);

        applyNewFoodList(diet, dailyDiet, foodList);
        diet.updateState(DietState.MODIFIED);
        return ResultResponse.of(ResultCode.DIET_OVERRIDE_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> markSticker(Long dailyDietId, Sticker sticker) {
        authenticatedProvider.getAuthenticatedParent();
        DailyDiet dailyDiet = 	getDailyDietOrThrow(dailyDietId);
        validateHasFood(dailyDiet);
        dailyDiet.markSticker(sticker);

        return ResultResponse.of(ResultCode.STICKER_MARK_SUCCESS, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getMonthlyStickersByParent(String month) {
        Child child = authenticatedProvider.getAuthenticatedChild();
        List<DailyDiet> diets = dailyDietRepository.findByChild(child);

        Map<String, Map<String, Sticker>> monthlyStickers = new HashMap<>();

        for (DailyDiet diet : diets) {
            String dietMonth = diet.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (dietMonth.equals(month)) {
                Sticker sticker = diet.getSticker();
                monthlyStickers
                        .computeIfAbsent(dietMonth, k -> new HashMap<>())
                        .put(diet.getDate().toString(), sticker);
            }
        }
        return ResultResponse.of(ResultCode.CALENDAR_OVERVIEW_SUCCESS, new MonthlyStickerResponseDto(child.getName(), monthlyStickers));
    }

    @Override
    @Transactional
    public ResultResponse<?> uploadPhoto(Long dietId, MultipartFile image) {
        authenticatedProvider.getAuthenticatedChild();
        Diet diet = getDietOrThrow(dietId);
        String imageUrl = s3UploadService.saveFile(image, imageUploadPath);
        diet.updateImage(imageUrl);
        return ResultResponse.of(ResultCode.DIET_ADD_IMAGE_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> updateDietState(Long dietId, DietState dietState){
        authenticatedProvider.getAuthenticatedChild();
        Diet diet = getDietOrThrow(dietId);
        diet.updateState(dietState);
        return ResultResponse.of(ResultCode.CHILD_STATE_UPLOAD_SUCCESS, null);
    }

    @Transactional
    DailyDiet getOrCreateDailyDiet(Child child, String dtoDate) {
        LocalDate date = LocalDate.parse(dtoDate);
        return dailyDietRepository.findByChildAndDate(child, date)
                .orElseGet(() -> {
                    DailyDiet newDailyDiet = new DailyDiet(child, date);
                    return dailyDietRepository.save(newDailyDiet);
                });
    }

    @Override
    @Transactional
    public ResultResponse<?> updateDietTime(Long dietId, String newTime) {
        authenticatedProvider.getAuthenticatedParent();
        Diet diet = getDietOrThrow(dietId);
        diet.updateTime(parseTimeOrThrow(newTime));
        return ResultResponse.of(ResultCode.DIET_EDIT_SUCCESS, null);
    }


    @Transactional(readOnly = true)
    public ResultResponse<?> getDietDetail(Long dietId) {
        authenticatedProvider.getAuthenticatedChild();
        Diet diet = getDietOrThrow(dietId);
        DietResponseDto response = toDietResponseDto(diet);
        return ResultResponse.of(ResultCode.DIET_DETAIL_RETRIEVAL_SUCCESS, response);
    }

    @Override
    @Transactional
    public ResultResponse<?> deletePhoto(Long dietId) {
        authenticatedProvider.getAuthenticatedChild();
        Diet diet = getDietOrThrow(dietId);

        if (diet.getImageUrl() != null) {
            s3UploadService.deleteFile(diet.getImageUrl());
            diet.updateImage(null);
        }

        return ResultResponse.of(ResultCode.CHILD_PHOTO_DELETE_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> deleteSticker(Long dailyDietId) {
        authenticatedProvider.getAuthenticatedParent();
        DailyDiet dailyDiet = getDailyDietOrThrow(dailyDietId);
        extractedSticker(dailyDiet);
        dailyDiet.markSticker(null);
        return ResultResponse.of(ResultCode.STICKER_DELETE_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> updateSticker(Long dailyDietId, Sticker sticker) {
        DailyDiet dailyDiet = getDailyDietOrThrow(dailyDietId);
        extractedSticker(dailyDiet);
        validateHasFood(dailyDiet);
        dailyDiet.markSticker(sticker);
        return ResultResponse.of(ResultCode.STICKER_UPDATE_SUCCESS, null);
    }

    private Diet getDietOrThrow(Long dietId) {
        Child authenticatedChild = authenticatedProvider.getAuthenticatedChild();
        Diet diet = dietRepository.findDietWithFoodList(dietId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND));

        if (!diet.getChild().equals(authenticatedChild))
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);

        return diet;
    }

    private DailyDiet getDailyDietOrThrow(Long dailyId) {
        Child authenticatedChild = authenticatedProvider.getAuthenticatedChild();
        DailyDiet dailyDiet = dailyDietRepository.findById(dailyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_DIET_NOT_FOUND));

        if (!dailyDiet.getChild().equals(authenticatedChild))
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);

        return dailyDiet;
    }

    private FoodApi findFoodOrThrow(Long foodId) {
        return foodApiRepository.findById(foodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND));
    }

    private LocalTime parseTimeOrThrow(String timeStr) {
        try {
            return LocalTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.INVALID_TIME_FORMAT);
        }
    }

    private static void extractedSticker(DailyDiet diet) {
        if (diet.getSticker() == null)
            throw new BusinessException(ErrorCode.STICKER_NOT_FOUND);
    }

    private static void validateHasFood(DailyDiet dailyDiet) {
        boolean hasFood = dailyDiet.getDiets().stream()
                .flatMap(diet -> diet.getFoodList().stream())
                .findAny()
                .isPresent();
        if (!hasFood) throw new BusinessException(ErrorCode.NO_FOOD_FOR_STICKER);
    }

    private List<Food> createFoodListFromDto(List<FoodItemDto> foodItemDtos, Parent parent) {
        if (foodItemDtos.isEmpty()) return new ArrayList<>();
        return foodItemDtos.stream()
                .map(foodItem -> {
                    if (foodItem.foodId() != null) {
                        FoodApi foodApi = foodApiRepository.findById(foodItem.foodId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.FOOD_NOT_FOUND));
                        return Food.fromFood(foodApi, foodItem.count());
                    } else {
                        CustomFood customFood = CustomFood.create(
                                parent,
                                foodItem.name(),
                                foodItem.kcal(),
                                foodItem.carbohydrate(),
                                foodItem.fat(),
                                foodItem.protein()
                        );
                        customFoodRepository.save(customFood);
                        return Food.fromFood(customFood, foodItem.count());
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Map<MealType, List<DietResponseDto>> groupDietsByMeal(List<Diet> diets) {
        return diets.stream().collect(Collectors.groupingBy(
                Diet::getMealType,
                Collectors.mapping(this::toDietResponseDto, Collectors.toList())
        ));
    }

    private DietResponseDto toDietResponseDto(Diet diet) {
        List<FoodResponseDto> foodDtos = diet.getFoodList().stream()
                .map(food -> new FoodResponseDto(
                        food.getName(),
                        food.getCalorie(),
                        food.getCarbohydrate(),
                        food.getFat(),
                        food.getProtein()
                )).toList();
        return new DietResponseDto(
                diet.getId(),
                diet.getImageUrl(),
                diet.getTime().toString(),
                diet.getState(),
                foodDtos,
                diet.getTotalCalorie(),
                diet.getTotalCarbohydrate(),
                diet.getTotalProtein(),
                diet.getTotalFat()
        );
    }

    private void applyNewFoodList(Diet diet, DailyDiet dailyDiet, List<Food> foodList, UpdateDietRequestDto dto) {
        diet.edit(foodList, dto);
        dailyDiet.recalculate();
    }

    private void applyNewFoodList(Diet diet, DailyDiet dailyDiet, List<Food> foodList) {
        diet.edit(foodList);
        dailyDiet.recalculate();
    }
}