
package com.project.growfit.domain.Diet.service.impl;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.FoodItemDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.dto.response.*;
import com.project.growfit.domain.Diet.entity.*;
import com.project.growfit.domain.Diet.repository.CustomFoodRepository;
import com.project.growfit.domain.Diet.repository.DailyDietRepository;
import com.project.growfit.domain.Diet.repository.DietRepository;
import com.project.growfit.domain.Diet.service.DietService;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.global.api.entity.FoodApi;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.auto.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import com.project.growfit.global.s3.service.S3UploadService;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietServiceImpl implements DietService {

    private final FoodApiRepository foodApiRepository;
    private final DailyDietRepository dailyDietRepository;
    private final ChildRepository childRepository;
    private final CustomFoodRepository customFoodRepository;
    private final DietRepository dietRepository;
    private final ParentRepository parentRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> searchFoods(CustomUserDetails user, String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<FoodApi> foodPage = foodApiRepository.findByFoodNmContaining(keyword, pageRequest);

        List<FoodResponseDto> response = foodPage.stream()
                .map(FoodResponseDto::toDto)
                .toList();

        if (response.isEmpty())
            return ResultResponse.of(ResultCode.DIET_SEARCH_RESULT_EMPTY, response);
        return ResultResponse.of(ResultCode.DIET_SEARCH_SUCCESS, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getFoodDetail(CustomUserDetails user, Long foodId) {
        FoodApi foodApi = findFoodOrThrow(foodId);
        FoodResponseDto response = FoodResponseDto.toDto(foodApi);

        return ResultResponse.of(ResultCode.DIET_DETAIL_RETRIEVAL_SUCCESS, response);
    }

    @Override
    @Transactional
    public ResultResponse<?> addDiet(CustomUserDetails user, AddDietRequestDto dto) {
        Parent parent = getAuthenticatedParent();
        Child child = findChildOfParent(parent);
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
        Parent parent = getAuthenticatedParent();
        Child child = findChildOfParent(parent);


        DailyDiet dailyDiet = dailyDietRepository.findById(dailyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_DIET_NOT_FOUND));

        if (!dailyDiet.getChild().equals(child)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        List<Diet> diets = dailyDiet.getDiets();

        Map<MealType, List<DietResponseDto>> dietsByMeal = diets.stream()
                .collect(Collectors.groupingBy(
                        Diet::getMealType,
                        Collectors.mapping(diet -> new DietResponseDto(
                                diet.getId(),
                                diet.getTime().toString(),
                                diet.getFoodList().stream()
                                        .map(food -> new FoodResponseDto(
                                                food.getName(),
                                                null,
                                                null,
                                                null,
                                                food.getCalorie(),
                                                food.getCarbohydrate(),
                                                food.getFat(),
                                                food.getProtein()
                                        ))
                                        .toList()
                        ), Collectors.toList())
                ));

        DailyDietResponseDto response = new DailyDietResponseDto(
                dailyDiet.getId(),
                dailyDiet.getDate().toString(),
                dietsByMeal
        );
        return ResultResponse.of(ResultCode.DIET_RETRIEVAL_SUCCESS, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getDailyDietByDate(CustomUserDetails user, String date) {
        Parent parent = getAuthenticatedParent();
        Child child = findChildOfParent(parent);

        LocalDate parsedDate = LocalDate.parse(date);
        DailyDiet dailyDiet = dailyDietRepository.findByChildAndDate(child, parsedDate)
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_DIET_NOT_FOUND));
        DailyDietResponseDto response = new DailyDietResponseDto(
                dailyDiet.getId(),
                dailyDiet.getDate().toString(),
                dailyDiet.getDiets().stream()
                        .collect(Collectors.groupingBy(
                                Diet::getMealType,
                                Collectors.mapping(diet -> new DietResponseDto(
                                        diet.getId(),
                                        diet.getTime().toString(),
                                        diet.getFoodList().stream()
                                                .map(food -> new FoodResponseDto(
                                                        food.getName(),
                                                        null,
                                                        null,
                                                        null,
                                                        food.getCalorie(),
                                                        food.getCarbohydrate(),
                                                        food.getFat(),
                                                        food.getProtein()
                                                ))
                                                .toList()
                                ), Collectors.toList())
                        ))
        );

        return ResultResponse.of(ResultCode.DAILY_DIET_RETRIEVAL_SUCCESS, response);
    }

    @Override
    @Transactional
    public ResultResponse<?> deleteDiet(Long dietId) {
        getAuthenticatedParent();
        Diet diet = dietRepository.findDietWithFoodList(dietId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND));
        dietRepository.delete(diet);

        return ResultResponse.of(ResultCode.DIET_DELETE_SUCCESS, null);

    }

    @Override
    @Transactional
    public ResultResponse<?> updateDiet(Long dietId, UpdateDietRequestDto dto) {
        Parent parent = getAuthenticatedParent();
        Diet diet = dietRepository.findDietWithFoodList(dietId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND));

        List<Food> foodList = createFoodListFromDto(dto.foodList(), parent);
        diet.edit(foodList, dto);
        return ResultResponse.of(ResultCode.DIET_EDIT_SUCCESS, null);
    }

    @Override
    @Transactional
    public ResultResponse<?> markSticker(Long dailyDietId, Sticker sticker) {
        getAuthenticatedParent();
        DailyDiet dailyDiet = dailyDietRepository.findById(dailyDietId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DAILY_DIET_NOT_FOUND));
        validateHasFood(dailyDiet);
        dailyDiet.markSticker(sticker);

        return ResultResponse.of(ResultCode.STICKER_MARK_SUCCESS, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse<?> getMonthlyStickersByParent(CustomUserDetails user, String month) {
        Child child;
        if (user.getRole().equals("ROLE_PARENT")) {
            Parent parent = parentRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            child = childRepository.findByParent(parent)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
        } else if (user.getRole().equals("ROLE_CHILD")) {
            child = childRepository.findByLoginId(user.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

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
    public DailyDiet getOrCreateDailyDiet(Child child, String dtoDate) {
        LocalDate date = LocalDate.parse(dtoDate);
        return dailyDietRepository.findByChildAndDate(child, date)
                .orElseGet(() -> {
                    DailyDiet newDailyDiet = new DailyDiet(child, date);
                    return dailyDietRepository.save(newDailyDiet);
                });
    }

    private FoodApi findFoodOrThrow(Long foodId) {
        return foodApiRepository.findById(foodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DIET_NOT_FOUND));
    }

    private Child findChildOfParent(Parent parent) {
        return childRepository.findByParent(parent)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }

    private List<Food> createFoodListFromDto(List<FoodItemDto> foodItemDtos, Parent parent) {
        if (foodItemDtos.isEmpty()) return new ArrayList<>();
        return foodItemDtos.stream()
                .map(foodItem -> {
                    if (foodItem.foodId() != null) {
                        FoodApi foodApi = foodApiRepository.findById(foodItem.foodId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.FOOD_NOT_FOUND));
                        return Food.fromFood(foodApi);
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
                        return Food.fromFood(customFood);
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static void validateHasFood(DailyDiet dailyDiet) {
        boolean hasFood = dailyDiet.getDiets().stream()
                .flatMap(diet -> diet.getFoodList().stream())
                .findAny()
                .isPresent();
        if (!hasFood) throw new BusinessException(ErrorCode.NO_FOOD_FOR_STICKER);
    }

    private Parent getAuthenticatedParent() {
        return (Parent) authenticatedUserProvider.getAuthenticatedUser();
    }
}
