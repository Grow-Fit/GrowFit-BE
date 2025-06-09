package com.project.growfit.global.auth.cookie;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * CookieService 단위 테스트 클래스.
 * - 목적: 쿠키 저장 로직이 정상적으로 동작하는지 검증
 * - HttpServletResponse를 mocking 하여 addHeader 동작 여부 확인
 */
class CookieServiceTest {
    private CookieProperties cookieProperties;
    private CookieService cookieService;
    private HttpServletResponse response;

    /**
     * 테스트 전 공통 초기화.
     * - CookieProperties 값을 직접 세팅하여 의존성 주입 시뮬레이션
     */
    @BeforeEach
    void setUp() {
        cookieProperties = new CookieProperties();
        setField(cookieProperties, "secure", true);
        setField(cookieProperties, "httpOnly", true);
        setField(cookieProperties, "sameSite", "Lax");
        setField(cookieProperties, "maxAge", 1800);

        cookieService = new CookieService(cookieProperties);
        response = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("AccessToken 쿠키 저장이 되어야한다.")
    void saveAccessTokenToCookie_shouldAddHeader() {
        String token = "test-token";

        cookieService.saveAccessTokenToCookie(response, token);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(response).addHeader(nameCaptor.capture(), valueCaptor.capture());

        assertThat(nameCaptor.getValue()).isEqualTo(HttpHeaders.SET_COOKIE);
        assertThat(valueCaptor.getValue()).contains("accessToken=test-token");
        assertThat(valueCaptor.getValue()).contains("HttpOnly");
        assertThat(valueCaptor.getValue()).contains("Secure");
        assertThat(valueCaptor.getValue()).contains("SameSite=Lax");
    }

    @Test
    @DisplayName("Email 쿠키 저장이 되어야한다.")
    void saveEmailToCookie_shouldAddHeader() {
        String email = "test@example.com";

        cookieService.saveEmailToCookie(response, email);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(response).addHeader(nameCaptor.capture(), valueCaptor.capture());

        assertThat(nameCaptor.getValue()).isEqualTo(HttpHeaders.SET_COOKIE);
        assertThat(valueCaptor.getValue()).contains("email=test@example.com");
    }

    /**
     * private 필드 주입을 위한 reflection 유틸 메서드.
     * @param target 대상 객체
     * @param fieldName 필드명
     * @param value 주입할 값
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}