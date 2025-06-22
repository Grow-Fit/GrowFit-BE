package com.project.growfit.domain.Diet.service.impl;

import com.project.growfit.domain.Diet.dto.request.AddDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.FoodItemDto;
import com.project.growfit.domain.Diet.dto.request.UpdateDietRequestDto;
import com.project.growfit.domain.Diet.dto.request.UpdateFoodListRequestDto;
import com.project.growfit.domain.Diet.dto.response.DietBasicDto;
import com.project.growfit.domain.Diet.entity.DailyDiet;
import com.project.growfit.domain.Diet.entity.Diet;
import com.project.growfit.domain.Diet.entity.DietState;
import com.project.growfit.domain.Diet.entity.MealType;
import com.project.growfit.domain.Diet.repository.CustomFoodRepository;
import com.project.growfit.domain.Diet.repository.DailyDietRepository;
import com.project.growfit.domain.Diet.repository.DietRepository;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.api.entity.FoodApi;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.s3.service.S3UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DietServiceImplTest {

    @InjectMocks
    private DietServiceImpl dietService;

    @Mock
    private FoodApiRepository foodApiRepository;
    @Mock
    private S3UploadService s3UploadService;
    @Mock
    private DailyDietRepository dailyDietRepository;
    @Mock
    private CustomFoodRepository customFoodRepository;
    @Mock
    private DietRepository dietRepository;
    @Mock
    private AuthenticatedUserProvider authenticatedProvider;

    private Parent mockParent;
    private Child mockChild;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockParent = new Parent("test@email.com", "부모", null, "kakao", "id", com.project.growfit.domain.User.entity.ROLE.ROLE_PARENT);
        mockChild = new Child("childId", "아이", com.project.growfit.domain.User.entity.ChildGender.MALE, 10, 120, 30, "pass", com.project.growfit.domain.User.entity.ROLE.ROLE_CHILD);
    }

    @Test
    @DisplayName("음식 키워드 검색 성공")
    void 음식검색_성공() {
        String keyword = "사과";
        FoodApi food = new FoodApi();
        ReflectionTestUtils.setField(food, "foodNm", "사과");
        Page<FoodApi> page = new PageImpl<>(List.of(food));

        when(authenticatedProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(foodApiRepository.findByFoodNmContaining(eq(keyword), any(PageRequest.class))).thenReturn(page);

        List<?> result = dietService.searchFoods(keyword, 0, 10);

        assertThat(result).hasSize(1);
        verify(foodApiRepository).findByFoodNmContaining(eq(keyword), any(PageRequest.class));
    }

    @Test
    @DisplayName("식단 추가 성공")
    void 식단추가_성공() {
        FoodApi food = new FoodApi();
        ReflectionTestUtils.setField(food, "foodNm", "사과");

        AddDietRequestDto dto = new AddDietRequestDto("2025-06-21", "12:00", MealType.LUNCH,
                List.of(new FoodItemDto(1L, null, null, null, null, 1.0, 1)));

        when(authenticatedProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(authenticatedProvider.getAuthenticatedChild()).thenReturn(mockChild);
        when(foodApiRepository.findById(1L)).thenReturn(Optional.of(food));
        when(dailyDietRepository.findByChildAndDate(any(), any())).thenReturn(Optional.empty());
        when(dailyDietRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DietBasicDto result = dietService.addDiet(dto);

        assertThat(result).isNotNull();
        verify(dailyDietRepository, times(2)).save(any(DailyDiet.class));
    }

    @Test
    @DisplayName("식단수정_성공")
    void 식단수정_성공() {
        // Given
        List<FoodItemDto> list = new ArrayList<>();
        FoodItemDto food = new FoodItemDto(1L, "닭가슴살", 123.0, 123.0, 123.0, 123.0, 1);
        list.add(food);
        UpdateDietRequestDto dto = new UpdateDietRequestDto("12:00", MealType.LUNCH, list);
        Diet mockDiet = mock(Diet.class);
        DailyDiet mockDailyDiet = mock(DailyDiet.class);

        when(authenticatedProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(authenticatedProvider.getAuthenticatedChild()).thenReturn(mockChild); // ⭐
        when(dietRepository.findDietWithFoodList(1L)).thenReturn(Optional.of(mockDiet));
        when(mockDiet.getChild()).thenReturn(mockChild);
        when(mockDiet.getDailyDiet()).thenReturn(mockDailyDiet);
        when(mockDiet.getTime()).thenReturn(LocalTime.NOON);

        when(foodApiRepository.findById(1L)).thenReturn(Optional.of(new FoodApi()));

        // When
        DietBasicDto result = dietService.updateDiet(1L, dto);

        // Then
        assertThat(result).isNotNull();
        verify(mockDiet).edit(anyList(), eq(dto));
        verify(mockDailyDiet).recalculate();
    }

    @Test
    @DisplayName("식단 삭제 성공")
    void 식단삭제_성공() {
        Diet mockDiet = mock(Diet.class);
        when(mockDiet.getChild()).thenReturn(mockChild);

        when(authenticatedProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(authenticatedProvider.getAuthenticatedChild()).thenReturn(mockChild);
        when(dietRepository.findDietWithFoodList(1L)).thenReturn(Optional.of(mockDiet));
        when(mockDiet.getTime()).thenReturn(LocalTime.NOON);

        DietBasicDto result = dietService.deleteDiet(1L);

        assertThat(result).isNotNull();
        verify(dietRepository).delete(mockDiet);
    }

    @Test
    @DisplayName("식단 오버라이드 성공")
    void 식단오버라이드_성공() {
        FoodApi food = new FoodApi();
        ReflectionTestUtils.setField(food, "foodNm", "바나나");

        Diet mockDiet = mock(Diet.class);
        DailyDiet mockDaily = mock(DailyDiet.class);
        when(mockDiet.getDailyDiet()).thenReturn(mockDaily);
        when(mockDiet.getChild()).thenReturn(mockChild);
        when(mockDiet.getTime()).thenReturn(LocalTime.NOON);

        when(authenticatedProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(authenticatedProvider.getAuthenticatedChild()).thenReturn(mockChild);
        when(dietRepository.findDietWithFoodList(1L)).thenReturn(Optional.of(mockDiet));
        when(foodApiRepository.findById(anyLong())).thenReturn(Optional.of(food));

        UpdateFoodListRequestDto dto = new UpdateFoodListRequestDto(List.of(new FoodItemDto(1L, null, null, null, null, 1.0, 1)));

        DietBasicDto result = dietService.overrideDietNutrition(1L, dto);

        assertThat(result).isNotNull();
        verify(mockDiet).edit(any());
        verify(mockDaily).recalculate();
    }

    @Test
    @DisplayName("식단 사진 제출 실패 - 이미지 없음")
    void 식단사진제출_실패_이미지없음() {
        Diet mockDiet = mock(Diet.class);
        when(mockDiet.getChild()).thenReturn(mockChild);
        when(authenticatedProvider.getAuthenticatedChild()).thenReturn(mockChild);
        when(dietRepository.findDietWithFoodList(1L)).thenReturn(Optional.of(mockDiet));

        assertThatThrownBy(() -> dietService.submitDiet(1L, null, DietState.MATCH))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.EMPTY_IMAGE_FILE.getMessage());
    }
    @Test
    @DisplayName("식단 상세 조회 성공")
    void 식단상세조회_성공() {
        Diet mockDiet = mock(Diet.class);
        when(mockDiet.getChild()).thenReturn(mockChild);
        when(mockDiet.getFoodList()).thenReturn(Collections.emptyList());
        when(mockDiet.getId()).thenReturn(1L);
        when(mockDiet.getImageUrl()).thenReturn("url");
        when(mockDiet.getTime()).thenReturn(LocalTime.NOON);
        when(mockDiet.getState()).thenReturn(DietState.MATCH);
        when(mockDiet.getTotalCalorie()).thenReturn(100.0);
        when(mockDiet.getTotalCarbohydrate()).thenReturn(10.0);
        when(mockDiet.getTotalProtein()).thenReturn(10.0);
        when(mockDiet.getTotalFat()).thenReturn(5.0);

        when(authenticatedProvider.getAuthenticatedChild()).thenReturn(mockChild);
        when(dietRepository.findDietWithFoodList(1L)).thenReturn(Optional.of(mockDiet));

        var result = dietService.getDietDetail(1L);

        assertThat(result).isNotNull();
        assertThat(result.dietId()).isEqualTo(1L);
        assertThat(result.imageUrl()).isEqualTo("url");
    }
}
