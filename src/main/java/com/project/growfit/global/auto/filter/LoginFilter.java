package com.project.growfit.global.auto.jwt.filter;

import com.project.growfit.global.auto.jwt.JwtProvider;
import com.project.growfit.global.auto.jwt.dto.CustomUserDetails;
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

    public LoginFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        setFilterProcessesUrl("/api/auth/sign-in");
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

        if (role.equals("ROLE_PARENT")){
            login_type = "SOCIAL_KAKAO";
        }else if (role.equals("ADMIN")){
            login_type = "ADMIN";
        }else {
            login_type = "LOCAL";
        }
        String token = jwtProvider.createAccessToken(user_id, role, login_type);

        jwtProvider.saveAccessTokenToCookie(response, token);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        log.info("[successfulAuthentication] JWT 토큰이 쿠키에 저장되었습니다.");
        }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn("[unsuccessfulAuthentication] 인증 실패: {}", failed.getMessage());
        response.setStatus(401);
    }
}
