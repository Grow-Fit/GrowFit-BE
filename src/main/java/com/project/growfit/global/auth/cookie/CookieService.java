package com.project.growfit.global.auth.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieService {

    private final CookieProperties cookieProperties;

    public void saveAccessTokenToCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = createCookie("accessToken", token);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("[saveAccessTokenToCookie] Access Token이 쿠키에 저장되었습니다.");
    }

    public void saveEmailToCookie(HttpServletResponse response, String email) {
        ResponseCookie cookie = createCookie("email", email);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        log.info("[saveEmailToCookie] 이메일이 쿠키에 저장되었습니다.");
    }

    public void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(cookieProperties.isHttpOnly());
        response.addCookie(cookie);
    }

    private ResponseCookie createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(cookieProperties.isHttpOnly())
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .maxAge(cookieProperties.getMaxAge())
                .path("/")
                .build();
    }
}
