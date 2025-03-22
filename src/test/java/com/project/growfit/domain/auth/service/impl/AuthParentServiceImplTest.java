package com.project.growfit.domain.auth.service.impl;

import com.google.zxing.WriterException;
import com.project.growfit.domain.auth.dto.request.RegisterChildRequest;
import com.project.growfit.domain.auth.dto.request.UpdateNicknameRequestDto;
import com.project.growfit.domain.auth.dto.response.ChildQrCodeResponseDto;
import com.project.growfit.domain.auth.entity.Child;
import com.project.growfit.domain.auth.entity.Parent;
import com.project.growfit.domain.auth.entity.ROLE;
import com.project.growfit.domain.auth.repository.ChildRepository;
import com.project.growfit.domain.auth.repository.ParentRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.response.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthParentServiceImplTest {

    @InjectMocks
    private AuthParentServiceImpl authParentService;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private ChildRepository childRepository;

    private Parent mockParent;
    private Child mockChild;
    private Child mockChild2;
    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() throws Exception {
        mockParent = new Parent("parent@test.com", "parentNickname", null, null, null, ROLE.ROLE_PARENT);
        setField(mockParent, "id", 1L);

        mockChild = new Child("child123", "childNickname", null, 10, 130, 30, null, ROLE.ROLE_CHILD);
        setField(mockChild, "childName", "childNickname");

        mockUserDetails = new CustomUserDetails(mockParent);
    }

    @Test
    @DisplayName("[updateParentNickname 성공 테스트] - 부모 닉네임 변경 성공")
    void updateParentNickname_Success() {
        // Given
        UpdateNicknameRequestDto request = new UpdateNicknameRequestDto("newNickname");
        when(parentRepository.findByEmail(mockUserDetails.getUserId())).thenReturn(Optional.of(mockParent));

        // When
        ResultResponse<?> response = authParentService.updateParentNickname(mockUserDetails, request);

        // Then
        assertThat(response).isNotNull();
        verify(parentRepository, times(1)).findByEmail(mockUserDetails.getUserId());
    }

    @Test
    @DisplayName("[updateParentNickname 실패 테스트] - 부모 정보 없음")
    void updateParentNickname_ParentNotFound() {
        // Given
        UpdateNicknameRequestDto request = new UpdateNicknameRequestDto("newNickname");
        when(parentRepository.findByEmail(mockUserDetails.getUserId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authParentService.updateParentNickname(mockUserDetails, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());

        verify(parentRepository, times(1)).findByEmail(mockUserDetails.getUserId());
    }

    @Test
    @DisplayName("[registerChild 성공 테스트] - 자녀 등록 성공")
    void registerChild_Success() {
        // Given
        RegisterChildRequest request = new RegisterChildRequest("childName", null, 10, 130, 30);
        when(parentRepository.findByEmail(mockUserDetails.getUserId())).thenReturn(Optional.of(mockParent));

        // When
        ResultResponse<?> response = authParentService.registerChild(mockUserDetails, request);

        // Then
        assertThat(response).isNotNull();
        verify(parentRepository, times(1)).findByEmail(mockUserDetails.getUserId());
    }

    @Test
    @DisplayName("[createQR 성공 테스트] - QR 코드 생성 성공")
    void createQR_Success() throws WriterException {
        // Given
        Long childId = 1L;
        when(childRepository.findByPid(childId)).thenReturn(Optional.of(mockChild));

        // When
        ResultResponse<?> response = authParentService.createQR(mockUserDetails, childId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getData()).isInstanceOf(ChildQrCodeResponseDto.class);
        verify(childRepository, times(1)).findByPid(childId);
    }

    @Test
    @DisplayName("[createQR 실패 테스트] - 자녀 정보 없음")
    void createQR_ChildNotFound() {
        // Given
        Long childId = 999L;
        when(childRepository.findByPid(childId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authParentService.createQR(mockUserDetails, childId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());

        verify(childRepository, times(1)).findByPid(childId);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}