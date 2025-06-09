package com.project.growfit.domain.auth.service.impl;

import com.project.growfit.domain.User.dto.request.AuthChildRequestDto;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.domain.User.service.impl.AuthChildServiceImpl;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.auth.service.CustomAuthenticationProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.redis.repository.TokenRedisRepository;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthChildServiceImplTest {

    @InjectMocks
    private AuthChildServiceImpl authChildService;

    @Mock
    private ChildRepository childRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenRedisRepository tokenRedisRepository;

    @Mock
    private CustomAuthenticationProvider authenticationProvider;

    @Mock
    private HttpServletResponse response;

    private Child mockChild;

    @BeforeEach
    void setUp() {
        mockChild = new Child("minjun", "김민준", null, 10, 130, 30, "testCode", null);
    }

    @Test
    @DisplayName("[findByCode 성공 테스트] - 아이 찾기 성공")
    void findByCode_Success() {
        // Given
        String code = "testCode";
        when(childRepository.findByCodeNumber(code)).thenReturn(Optional.of(mockChild));

        // When
        ResultResponse<?> response = authChildService.findByCode(code);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getData()).isInstanceOf(ChildInfoResponseDto.class);
        verify(childRepository, times(1)).findByCodeNumber(code);
    }

    @Test
    @DisplayName("[findByCode 실패 테스트] 아이를 찾을 수 없음")
    void findByCode_NotFound() {
        // Given
        String code = "invalidCode";
        when(childRepository.findByCodeNumber(code)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authChildService.findByCode(code))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CHILD_NOT_FOUND.getMessage());

        verify(childRepository, times(1)).findByCodeNumber(code);
    }

    @Test
    @DisplayName("[registerChildCredentials 성공 테스트] 아이 계정 등록 성공 테스트")
    void registerChildCredentials_Success() {
        // Given
        Long childId = 1L;
        AuthChildRequestDto request = new AuthChildRequestDto("childTestId", "password123", "민준콩");
        when(childRepository.findById(childId)).thenReturn(Optional.of(mockChild));
        when(passwordEncoder.encode(request.childPassword())).thenReturn("encodedPassword");

        // When
        ResultResponse<?> response = authChildService.registerChildCredentials(childId, request);

        // Then
        assertThat(response).isNotNull();
        verify(childRepository, times(1)).save(mockChild);
    }

    @Test
    @DisplayName("[registerChildCredentials 실패 테스트] 아이를 찾을 수 없음")
    void registerChildCredentials_NotFound() {
        // Given
        Long childId = 999L;
        AuthChildRequestDto request = new AuthChildRequestDto("childTestId", "password123", "민준콩");
        when(childRepository.findById(childId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authChildService.registerChildCredentials(childId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CHILD_NOT_FOUND.getMessage());

        verify(childRepository, times(1)).findById(childId);
    }

    @Test
    @DisplayName("[findChildPassword 성공 테스트] 비밀번호 변경 성공")
    void findChildPassword_Success() {
        // Given
        FindChildPasswordRequestDto request = new FindChildPasswordRequestDto("childTestId", "testCode", "newPassword");
        when(childRepository.existsByCodeNumberAndLoginId(request.code(), request.user_id())).thenReturn(true);
        when(childRepository.findByCodeNumber(request.code())).thenReturn(Optional.of(mockChild));

        // When
        ResultResponse<?> response = authChildService.findChildPassword(request);

        // Then
        assertThat(response).isNotNull();
        verify(childRepository, times(1)).save(mockChild);
    }

    @Test
    @DisplayName("[findChildPassword 실패 테스트] 아이를 찾을 수 없음")
    void findChildPassword_NotFound() {
        // Given
        FindChildPasswordRequestDto request = new FindChildPasswordRequestDto("childTestId", "invalidCode", "newPassword");
        when(childRepository.existsByCodeNumberAndLoginId(request.code(), request.user_id())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authChildService.findChildPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CHILD_NOT_FOUND.getMessage());

        verify(childRepository, times(1)).existsByCodeNumberAndLoginId(request.code(), request.user_id());
    }
}