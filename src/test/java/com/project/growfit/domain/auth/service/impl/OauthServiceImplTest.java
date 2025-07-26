package com.project.growfit.domain.auth.service.impl;

import com.project.growfit.domain.auth.dto.request.ParentOAuthRequestDto;
import com.project.growfit.domain.auth.dto.response.ParentResponseDto;
import com.project.growfit.domain.User.entity.Parent;
import com.project.growfit.domain.User.entity.ROLE;
import com.project.growfit.domain.User.repository.ParentRepository;
import com.project.growfit.global.auth.cookie.CookieService;
import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.auth.service.AuthenticatedUserProvider;
import com.project.growfit.global.redis.repository.TokenRedisRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OauthServiceImplTest {

    @InjectMocks
    private OauthServiceImpl oauthService;
    @Mock
    private TokenRedisRepository tokenRedisRepository;
    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;
    @Mock
    private CookieService cookieService;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private JwtProvider jwtProvider;
    private RestTemplate restTemplate = mock(RestTemplate.class);
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(oauthService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(oauthService, "kakaoLogoutUri", "https://kapi.kakao.com/v1/user/logout");
        ReflectionTestUtils.setField(oauthService, "userInfoReqUri", "https://kapi.kakao.com/v2/user/me");
    }

    @Test
    void 카카오_소셜로그인_성공() {
        ParentOAuthRequestDto dto = new ParentOAuthRequestDto("e", "n", "i");
        Parent parent = new Parent("e", "n", null, "kakao", "i", ROLE.ROLE_PARENT);
        when(parentRepository.save(any())).thenReturn(parent);
        Long result = oauthService.signUp(dto);
        assertEquals(parent.getId(), result);
    }

    @Test
    void 카카오_소셜로그아웃_성공() {
        Parent parent = new Parent("test@example.com", "name", "photo", "kakao", "kakaoId", ROLE.ROLE_PARENT);
        ReflectionTestUtils.setField(parent, "id", 1L);
        when(authenticatedUserProvider.getAuthenticatedParent()).thenReturn(parent);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        String str = oauthService.kakaoLogout("dummyAccessToken", response);

        assertEquals(str, "Parent Id [" + parent.getId() + "] 로그아웃 완료");
        verify(cookieService).clearCookie(response, "accessToken");
    }


    @Test
    void 카카오_식별자로_부모를_조회할_때_존재하면_응답을_반환한다() {
        Parent parent = new Parent("test@example.com", "name", "photo", "kakao", "kid", ROLE.ROLE_PARENT);
        when(parentRepository.findParentByProviderId("kid")).thenReturn(Optional.of(List.of(parent)));
        ParentResponseDto response = oauthService.findByUserKakaoIdentifier("kid");
        assertNotNull(response);
        assertEquals("test@example.com", response.email());
    }

    @Test
    void 카카오_식별자로_부모를_조회할_때_존재하지_않으면_null을_반환한다() {
        when(parentRepository.findParentByProviderId("kid")).thenReturn(Optional.of(List.of()));
        ParentResponseDto response = oauthService.findByUserKakaoIdentifier("kid");
        assertNull(response);
    }
}