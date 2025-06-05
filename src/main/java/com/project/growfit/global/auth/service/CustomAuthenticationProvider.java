package com.project.growfit.global.auth.service;

import com.project.growfit.global.auth.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String user_id = (String) authentication.getPrincipal();
        String user_password = (String) authentication.getCredentials();
        log.info("[authenticate] 인증 요청 시작: user_id = {}", user_id);

        CustomUserDetails userDetails;
        try {
            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user_id);
        } catch (UsernameNotFoundException e) {
            log.warn("[authenticate] 사용자 정보 조회 실패: user_id = {}", user_id);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        } catch (Exception e) {
            log.error("[authenticate] 사용자 정보 조회 중 오류 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.DATA_PROCESSING_ERROR);
        }

        log.info("[authenticate] 사용자 정보 조회 성공: user_id = {}", user_id);
        if (!passwordEncoder.matches(user_password, userDetails.getPassword())) {
            log.warn("[authenticate] 비밀번호 검증 실패: user_id = {}", user_id);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        log.info("[authenticate] 인증 성공: user_id = {}", user_id);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
