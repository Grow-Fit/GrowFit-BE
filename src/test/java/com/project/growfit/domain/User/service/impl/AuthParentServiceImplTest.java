package com.project.growfit.domain.User.service.impl;

import com.project.growfit.domain.User.dto.request.AuthParentRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.dto.response.ChildQrCodeResponseDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.growfit.domain.User.entity.ChildGender.MALE;
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

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private CustomUserDetails mockUser;
    private Parent mockParent;

    @BeforeEach
    void setUp() {
        mockUser = new CustomUserDetails("parent@example.com", "ROLE_PARENT");
        mockParent = new Parent("parent@example.com", "ParentName", "", "kakao", ROLE.ROLE_PARENT);
    }

    @Test
    @DisplayName("자녀 등록 성공 테스트")
    void 자녀_등록을_성공한다() {
        // Given
        AuthParentRequestDto request = new AuthParentRequestDto("김아이", "아이", MALE, 10, 130, 30);
        when(authenticatedUserProvider.getAuthenticatedParent()).thenReturn(mockParent);

        // When
        ChildInfoResponseDto dto = authParentService.registerChild(request);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.child_name()).isEqualTo("아이");
        verify(childRepository, times(1)).save(any(Child.class));
    }

    @Test
    @DisplayName("중복 자녀 등록 실패 테스트")
    void 중복_자녀를_등록_시_자녀_등록에_실패한다() {
        // Given
        AuthParentRequestDto request = new AuthParentRequestDto("아이", "김아이", MALE, 10, 130, 30);
        mockParent.addChild(new Child(null, "김아이", MALE, 10, 130, 30, null, ROLE.ROLE_CHILD));

        // Mock 설정 추가
        when(authenticatedUserProvider.getAuthenticatedParent()).thenReturn(mockParent);

        // When & Then
        assertThatThrownBy(() -> authParentService.registerChild(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CHILD_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("QR 생성 성공 테스트")
    void 자녀_등록_QR을_생성한다() throws Exception {
        // Given
        Child child = new Child("childId", "nickname", "pass", ROLE.ROLE_CHILD);
        when(authenticatedUserProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(authenticatedUserProvider.getAuthenticatedChild()).thenReturn(child);

        // When
        ChildQrCodeResponseDto dto = authParentService.createQR();

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.qr_code()).isNotBlank();
        assertThat(dto.code()).isNotBlank();
    }

    @Test
    @DisplayName("QR 중복 생성 실패 테스트")
    void 이미_QR코드를_생성한_경우_실패한다() {
        // Given
        Child child = new Child("childId", "nickname", "pass", ROLE.ROLE_CHILD);
        child.updateCode("existing-code");
        when(authenticatedUserProvider.getAuthenticatedParent()).thenReturn(mockParent);
        when(authenticatedUserProvider.getAuthenticatedChild()).thenReturn(child);

        // When & Then
        assertThatThrownBy(() -> authParentService.createQR())
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.QR_ALREADY_EXISTS.getMessage());
    }
}