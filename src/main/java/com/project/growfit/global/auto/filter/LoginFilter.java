package com.project.growfit.global.auto.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.auto.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("user_id");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[attemptAuthentication] 인증 시도 시작");
        String user_id = obtainUsername(request);
        String user_password = request.getParameter("user_password");

        log.info("[attemptAuthentication] 로그인 시도 사용자 아이디: {}", user_id);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user_id, user_password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("[successfulAuthentication] 인증 성공! JWT 토큰 생성 시작");
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new ClassCastException("Authentication Principal이 CustomUserDetails 타입이 아닙니다. 실제 타입: "
                    + principal.getClass().getName());
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String user_id = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String login_type;

        login_type = switch (role) {
            case "ROLE_PARENT" -> "SOCIAL_KAKAO";
            case "ADMIN" -> "ADMIN";
            default -> "LOCAL";
        };

        String token = jwtProvider.createAccessToken(user_id, role, login_type);

        jwtProvider.saveAccessTokenToCookie(response, token);

        log.info("[successfulAuthentication] 로그인 성공 - 사용자 ID: {}, 역할: {}, JWT 저장 완료", user_id, role);

        // 응답 JSON 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new AuthenticationResponse(true, "로그인 성공", token)));
        }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn("[unsuccessfulAuthentication] 로그인 실패: {}", failed.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 형식으로 실패 메시지 반환
        response.getWriter().write(objectMapper.writeValueAsString(new AuthenticationResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다.", null)));
    }

    private record AuthenticationResponse(boolean success, String message, String token) {}
}
