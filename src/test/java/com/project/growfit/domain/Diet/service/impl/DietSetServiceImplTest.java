package com.project.growfit.domain.Diet.service.impl;


import com.project.growfit.domain.Diet.dto.request.SaveDietSetRequestDto;
import com.project.growfit.domain.Diet.dto.request.FoodItemDto;
import com.project.growfit.domain.Diet.dto.response.DietSetBasicDto;
import com.project.growfit.domain.Diet.entity.DietSet;
import com.project.growfit.domain.Diet.repository.CustomFoodRepository;
import com.project.growfit.domain.Diet.repository.DietSetRepository;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DietSetServiceImplTest {

    @Mock private DietSetRepository dietSetRepository;
    @Mock private CustomFoodRepository customFoodRepository;
    @Mock private FoodApiRepository foodApiRepository;
    @Mock private AuthenticatedUserProvider authenticatedProvider;
    @Mock private Parent mockParent;

    @InjectMocks
    private DietSetServiceImpl dietSetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("식단 세트 저장 성공")
    void saveDietSetSuccess() {
        // Given
        when(authenticatedProvider.getAuthenticatedParent()).thenReturn(mockParent);
        SaveDietSetRequestDto dto = new SaveDietSetRequestDto("세트A", List.of(
                new FoodItemDto(null, "사과", 52.0, 14.0, 0.2, 0.3, 1)
        ));

        // When
        DietSetBasicDto result = dietSetService.saveDietSet(dto);

        // Then
        assertThat(result).isNotNull();
        verify(dietSetRepository, times(1)).save(any(DietSet.class));
    }

    @Test
    @DisplayName("식단 세트 삭제 실패 - 접근 권한 없음")
    void deleteDietSetForbidden() {
        // Given
        DietSet fakeSet = mock(DietSet.class);
        when(authenticatedProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(fakeSet.getParent()).thenReturn(mock(Parent.class));
        when(dietSetRepository.findById(1L)).thenReturn(Optional.of(fakeSet));

        // When / Then
        assertThatThrownBy(() -> dietSetService.deleteDietSet(1L))
                .isInstanceOf(RuntimeException.class); // 실제 BusinessException으로 바꿔도 됨
    }
}