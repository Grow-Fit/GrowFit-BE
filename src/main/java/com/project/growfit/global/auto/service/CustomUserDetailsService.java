package com.project.growfit.global.auto.service;

import com.project.growfit.domain.auto.entity.Child;
import com.project.growfit.domain.auto.repository.ChildRepository;
import com.project.growfit.global.auto.dto.CustomUserDetails;
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

    public UserDetails loadUserByUsername(String user_id) throws UsernameNotFoundException {
        log.info("[loadUserByUsername] 사용자 id 찾는 중: {}", user_id);

        Child child = childRepository.findByChildId(user_id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + user_id));

        log.info("[loadUserByUsername] 사용자 정보 조회 성공: user_id = {}", user_id);
        return new CustomUserDetails(child);
    }
}
