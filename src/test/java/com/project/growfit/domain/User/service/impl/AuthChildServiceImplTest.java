package com.project.growfit.domain.User.service.impl;

import com.project.growfit.domain.User.dto.request.AuthChildRequestDto;
import com.project.growfit.domain.User.dto.request.FindChildPasswordRequestDto;
import com.project.growfit.domain.User.dto.response.ChildInfoResponseDto;
import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.global.auth.cookie.CookieService;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.auth.service.CustomAuthenticationProvider;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import com.project.growfit.global.redis.entity.TokenRedis;
import com.project.growfit.global.redis.repository.TokenRedisRepository;
import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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
    private AuthenticatedUserProvider authenticatedUser;

    @Mock
    private CookieService cookieService;

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
    @DisplayName("[findChildID 성공 테스트] - 아이 ID 찾기 성공")
    void 알맞은_코드를_입력_시_아이_정보를_반환한다() {
        // Given
        String code = "testCode";
        when(childRepository.findByCodeNumber(code)).thenReturn(Optional.of(mockChild));

        // When
        ResultResponse<?> response = authChildService.findChildID(code);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getData()).isInstanceOf(ChildInfoResponseDto.class);
        verify(childRepository, times(1)).findByCodeNumber(code);
    }

    @Test
    @DisplayName("[findByCode 실패 테스트] 아이를 찾을 수 없음")
    void 틀린_코드를_입력_시_아이_정보를_반환하지_않는다() {
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
    void 아이_계졍을_성공적으로_등록한다() {
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
    void 아이_정보가_없는_경우_아이_등록에_실패한다() {
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
    void 아이의_비밀번호를_성공적으로_변경한다() {
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
    @DisplayName("[findChildPassword 실패 테스트] 비밀번호 변경 실패")
    void 아이의_비밀번호_변경을_실패한다() {
        // Given
        FindChildPasswordRequestDto request = new FindChildPasswordRequestDto("childTestId", "invalidCode", "newPassword");
        when(childRepository.existsByCodeNumberAndLoginId(request.code(), request.user_id())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authChildService.findChildPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CHILD_NOT_FOUND.getMessage());

        verify(childRepository, times(1)).existsByCodeNumberAndLoginId(request.code(), request.user_id());
    }

    @Test
    @DisplayName("[login 성공 테스트] 아이 로그인 성공")
    void 아이_로그인에_성공한다() {
        // Given
        AuthChildRequestDto request = new AuthChildRequestDto("childTestId", "password123", null);
        Child mockChild = new Child("childTestId", "닉네임", "encodedPassword", ROLE.ROLE_CHILD);

        Authentication mockAuthentication = mock(Authentication.class);

        when(authenticationProvider.authenticate(any())).thenReturn(mockAuthentication);
        when(childRepository.findByLoginId("childTestId")).thenReturn(Optional.of(mockChild));
        when(jwtProvider.createAccessToken(any(), any(), any())).thenReturn("access-token");
        when(jwtProvider.createRefreshToken(any())).thenReturn("refresh-token");

        // When
        ResultResponse<?> result = authChildService.login(request, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ResultCode.LOGIN_SUCCESS.getStatus().value());
        verify(authenticationProvider, times(1)).authenticate(any());
        verify(childRepository, times(1)).findByLoginId("childTestId");
        verify(tokenRedisRepository, times(1)).save(any(TokenRedis.class));
        verify(cookieService, times(1)).saveAccessTokenToCookie(response, "access-token");
    }

    @Test
    @DisplayName("[logout 성공 테스트] 아이 로그아웃 성공")
    void 아이_로그아웃을_성공한다() {
        // Given
        String loginId = "childTestId";
        Child mockChild = new Child(loginId, "닉네임", "암호화된비밀번호", ROLE.ROLE_CHILD);

        when(authenticatedUser.getAuthenticatedChild()).thenReturn(mockChild);
        doNothing().when(tokenRedisRepository).deleteById(loginId);
        doNothing().when(cookieService).clearCookie(response, "accessToken");

        ResultResponse<String> result = authChildService.logout(response);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(200);
        verify(authenticatedUser, times(1)).getAuthenticatedChild();
        verify(tokenRedisRepository, times(1)).deleteById(loginId);
        verify(cookieService, times(1)).clearCookie(response, "accessToken");
    }
}