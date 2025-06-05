package com.project.growfit.global.auth.filter;

import com.project.growfit.global.auth.jwt.JwtProvider;
import com.project.growfit.global.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtCookieAuthenticationFilter extends GenericFilterBean {

    private final JwtProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.debug("[JwtCookieAuthenticationFilter] >>> Entering JWT 쿠키 필터");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Optional<Cookie> accessTokenCookie = CookieUtils.getCookie(request, "accessToken");
        if (accessTokenCookie.isPresent()) {
            String accessToken = accessTokenCookie.get().getValue();
            UsernamePasswordAuthenticationToken authentication;
            if (!jwtTokenProvider.isExpired(accessToken)) {
                log.info("[doFilter] 유효한 토큰 확인됨.");
                authentication = jwtTokenProvider.createAuthenticationFromToken(accessToken);
            } else {
                log.info("[doFilter] 토큰 만료됨. 재발급 시도.");
                authentication = jwtTokenProvider.replaceAccessToken(response, accessToken);
            }
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("[doFilter] 인증 정보 등록 완료.");
            }
        }else{
            log.info("[doFilter] accessToken 쿠키가 존재하지 않음.");
        }
        filterChain.doFilter(request, response);
    }
}