package com.project.growfit.global.auth.cookie;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * CookieProperties 클래스의 @ConfigurationProperties 바인딩 테스트
 */
@SpringBootTest
@EnableConfigurationProperties(value = CookieProperties.class)
@TestPropertySource(properties = {
        "jwt.secret_key=test_secret_key",
        "custom.cookie.secure=true",
        "custom.cookie.http-only=false",
        "custom.cookie.same-site=None",
        "custom.cookie.max-age=1800"
})
class CookiePropertiesTest {

    @Autowired
    private CookieProperties cookieProperties;

    @Test
    @DisplayName("application.yml 값이 정상적으로 바인딩되어야 한다")
    void testCookiePropertiesBinding() {
        assertThat(cookieProperties.isSecure()).isTrue();
        assertThat(cookieProperties.isHttpOnly()).isFalse();
        assertThat(cookieProperties.getSameSite()).isEqualTo("None");
        assertThat(cookieProperties.getMaxAge()).isEqualTo(1800);
    }
}