package com.project.growfit.global.auto.service;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.repository.ChildRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
import com.project.growfit.global.exception.BusinessException;
import com.project.growfit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ChildRepository childRepository;

    @Override
    public UserDetails loadUserByUsername(String user_id) throws UsernameNotFoundException {
        log.info("[loadUserByUsername] 사용자 정보 조회 시도: user_id = {}", user_id);

        Child child = childRepository.findByLoginId(user_id).orElse(null);
        if (child != null) {
            log.info("[loadUserByUsername] 자녀 계정 조회 성공: childId = {}", user_id);
            return new CustomUserDetails(child);
        }

        log.warn("[loadUserByUsername] 사용자 정보 조회 실패: user_id = {}", user_id);
        throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
}
